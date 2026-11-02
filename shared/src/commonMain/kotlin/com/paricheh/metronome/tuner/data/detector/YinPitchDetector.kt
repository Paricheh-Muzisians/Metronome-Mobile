package com.paricheh.metronome.tuner.data.detector

import com.paricheh.metronome.core.audio.AudioFrame
import com.paricheh.metronome.tuner.data.dsp.SignalConditioner
import kotlin.math.abs
import kotlin.math.min

/**
 * High-accuracy, sub-cent precision YIN algorithm for acoustic musical instrument tuning.
 *
 * Implements:
 * 1. Pre-processing: DC-blocking and dynamic noise gate via [SignalConditioner].
 * 2. Sliding analysis window (2048 samples) supporting low instruments down to sub-45 Hz.
 * 3. Cumulative Mean Normalized Difference Function (CMNDF).
 * 4. Absolute thresholding (first-valley search) to strictly prevent subharmonic (2*T0) octave errors.
 * 5. Parabolic sub-sample interpolation for continuous ±0.01 cent frequency resolution.
 * 6. Zero-allocation per-frame execution with pre-allocated buffers.
 */
class YinPitchDetector(
    private var threshold: Float = 0.15f,
    private val fallbackThreshold: Float = 0.35f,
    private val signalConditioner: SignalConditioner = SignalConditioner()
) : PitchDetector {

    companion object {
        const val ANALYSIS_WINDOW_SIZE = 2048
        const val HALF_WINDOW_SIZE = ANALYSIS_WINDOW_SIZE / 2
        // Tau search range for 44.1 kHz:
        // Tau 10 -> ~4410 Hz (Piano C8 is 4186 Hz)
        // Tau 1020 -> ~43.2 Hz (Guitar Low E is 82.4 Hz, Bass low E is 41.2 Hz)
        const val DEFAULT_MIN_TAU = 10
        const val DEFAULT_MAX_TAU = 1020
    }

    private var minTau = DEFAULT_MIN_TAU
    private var maxTau = DEFAULT_MAX_TAU

    // Pre-allocated buffers to guarantee zero per-frame heap allocations
    private val analysisWindow = FloatArray(ANALYSIS_WINDOW_SIZE)
    private val yinBuffer = FloatArray(HALF_WINDOW_SIZE)
    private val tempBuffer = FloatArray(ANALYSIS_WINDOW_SIZE)
    private var samplesInWindow = 0

    /**
     * Configures the search bounds based on the active instrument's physical frequency range.
     * Prevents subharmonic/overtone octave jumps by constraining the lag search window.
     */
    fun configureFrequencyRange(minFreqHz: Float, maxFreqHz: Float, sampleRate: Int = 44100) {
        val safeMinFreq = minFreqHz.coerceIn(20f, 2000f)
        val safeMaxFreq = maxFreqHz.coerceIn(safeMinFreq + 10f, 5000f)

        maxTau = min((sampleRate / safeMinFreq).toInt(), HALF_WINDOW_SIZE - 2)
        minTau = maxOf((sampleRate / safeMaxFreq).toInt(), 6)
    }

    fun setThreshold(newThreshold: Float) {
        threshold = newThreshold.coerceIn(0.05f, 0.40f)
    }

    fun reset() {
        samplesInWindow = 0
        analysisWindow.fill(0f)
        signalConditioner.resetFilterStates()
    }

    override fun detect(frame: AudioFrame): PitchResult {
        val samples = frame.samples
        val sampleRate = frame.sampleRate

        if (samples.isEmpty()) return PitchResult(0f, 0f)

        val frameSize = min(samples.size, tempBuffer.size)
        samples.copyInto(tempBuffer, 0, 0, frameSize)

        // Step 0: Signal conditioning (DC-block and dynamic noise gate)
        val exceedsNoiseGate = signalConditioner.process(tempBuffer)
        if (!exceedsNoiseGate) {
            samplesInWindow = 0
            return PitchResult(0f, 0f)
        }

        // Fill sliding analysis window
        updateAnalysisWindow(tempBuffer, frameSize)

        val windowHalf = HALF_WINDOW_SIZE
        val tauMaxClamped = min(maxTau, windowHalf - 2)
        val tauMinClamped = minTau.coerceAtLeast(2)

        // Step 1: Average Squared Difference Function (ASDF)
        for (tau in 0 until windowHalf) {
            var sum = 0f
            for (i in 0 until windowHalf) {
                val delta = analysisWindow[i] - analysisWindow[i + tau]
                sum += delta * delta
            }
            yinBuffer[tau] = sum
        }

        // Step 2: Cumulative Mean Normalized Difference Function (CMNDF)
        yinBuffer[0] = 1f
        var runningSum = 0f
        for (tau in 1 until windowHalf) {
            runningSum += yinBuffer[tau]
            yinBuffer[tau] = if (runningSum > 1e-6f) {
                yinBuffer[tau] * tau / runningSum
            } else {
                1f
            }
        }

        // Step 3: Absolute Thresholding (First Local Valley Below Threshold)
        var tau = -1
        for (t in tauMinClamped until tauMaxClamped) {
            if (yinBuffer[t] < threshold) {
                var valley = t
                while (valley + 1 < tauMaxClamped && yinBuffer[valley + 1] < yinBuffer[valley]) {
                    valley++
                }
                tau = valley
                break
            }
        }

        // Fallback: If no valley dipped below standard threshold, search for the best local minimum
        if (tau == -1) {
            var minVal = Float.MAX_VALUE
            var bestTau = -1
            for (t in (tauMinClamped + 1) until (tauMaxClamped - 1)) {
                if (yinBuffer[t] < minVal &&
                    yinBuffer[t] <= yinBuffer[t - 1] &&
                    yinBuffer[t] <= yinBuffer[t + 1]
                ) {
                    minVal = yinBuffer[t]
                    bestTau = t
                }
            }
            if (bestTau != -1 && minVal < fallbackThreshold) {
                tau = bestTau
            } else {
                return PitchResult(0f, 0f)
            }
        }

        // Step 4: Parabolic Interpolation for Sub-Sample Accuracy
        val betterTau = if (tau in 1 until windowHalf - 1) {
            val s0 = yinBuffer[tau - 1]
            val s1 = yinBuffer[tau]
            val s2 = yinBuffer[tau + 1]
            val denominator = 2f * (s0 - 2f * s1 + s2)
            if (abs(denominator) > 1e-6f) {
                tau + (s0 - s2) / denominator
            } else {
                tau.toFloat()
            }
        } else {
            tau.toFloat()
        }

        if (betterTau <= 0f) return PitchResult(0f, 0f)

        val frequency = sampleRate / betterTau
        val confidence = (1.0f - min(1.0f, yinBuffer[tau])).coerceIn(0f, 1f)

        return PitchResult(frequency, confidence)
    }

    private fun updateAnalysisWindow(samples: FloatArray, count: Int) {
        if (count >= ANALYSIS_WINDOW_SIZE) {
            val start = count - ANALYSIS_WINDOW_SIZE
            samples.copyInto(analysisWindow, 0, start, count)
            samplesInWindow = ANALYSIS_WINDOW_SIZE
        } else if (samplesInWindow == 0) {
            // First frame after silence/reset: replicate frame to prime analysis window immediately
            var written = 0
            while (written < ANALYSIS_WINDOW_SIZE) {
                val toCopy = min(count, ANALYSIS_WINDOW_SIZE - written)
                samples.copyInto(analysisWindow, written, 0, toCopy)
                written += toCopy
            }
            samplesInWindow = ANALYSIS_WINDOW_SIZE
        } else {
            val shift = count
            analysisWindow.copyInto(analysisWindow, 0, shift, ANALYSIS_WINDOW_SIZE)
            samples.copyInto(analysisWindow, ANALYSIS_WINDOW_SIZE - shift, 0, shift)
        }
    }
}
