package com.paricheh.metronome.tuner.data.detector

import com.paricheh.metronome.core.audio.AudioFrame
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Enhanced implementation of the YIN algorithm for robust pitch detection.
 *
 * Features:
 * 1. RMS signal gate (0.005f / -46 dBFS) to suppress ambient microphone noise in silent rooms.
 * 2. Sliding analysis window (2048 samples) decoupled from platform buffer sizes.
 * 3. Pre-allocated buffers to prevent real-time garbage collection pauses.
 * 4. Bounded tau search (40 Hz to 4410 Hz) preventing unphysical frequency outputs.
 * 5. Subharmonic Octave Lock Guard to prevent octave jumps during harmonic decay.
 * 6. Elimination of loose fallback thresholds that produce false noise pitches.
 */
class YinPitchDetector(
    private val threshold: Float = 0.12f,
    private val rmsThreshold: Float = 0.005f, // -46 dBFS noise gate
    private val subharmonicThreshold: Float = 0.22f // Relaxed threshold for fundamental F0
) : PitchDetector {

    companion object {
        private const val ANALYSIS_WINDOW_SIZE = 2048
        private const val HALF_WINDOW_SIZE = ANALYSIS_WINDOW_SIZE / 2
        private const val MIN_TAU = 10 // 44100 / 10 = 4410 Hz (C8)
        private const val MAX_TAU = 1102 // 44100 / 1102 = 40 Hz (E1/G1)
    }

    // Pre-allocated buffers to guarantee zero per-frame heap allocations
    private val analysisWindow = FloatArray(ANALYSIS_WINDOW_SIZE)
    private val yinBuffer = FloatArray(HALF_WINDOW_SIZE)

    override fun detect(frame: AudioFrame): PitchResult {
        val samples = frame.samples
        val sampleRate = frame.sampleRate

        if (samples.isEmpty()) return PitchResult(0f, 0f)

        // Step 0: RMS Energy Gate check
        val rms = calculateRms(samples)
        if (rms < rmsThreshold) {
            return PitchResult(0f, 0f)
        }

        // Fill sliding analysis window
        updateAnalysisWindow(samples)

        val windowHalf = HALF_WINDOW_SIZE
        val tauMax = min(MAX_TAU, windowHalf - 1)
        val tauMin = MIN_TAU

        // Step 1: Difference Function
        for (tau in 0 until windowHalf) {
            yinBuffer[tau] = 0f
            for (i in 0 until windowHalf) {
                val delta = analysisWindow[i] - analysisWindow[i + tau]
                yinBuffer[tau] += delta * delta
            }
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

        // Step 3: Absolute Threshold Search with Subharmonic Octave Guard
        var tau = -1
        for (t in tauMin until tauMax) {
            if (yinBuffer[t] < threshold) {
                tau = t
                // Find first local minimum
                while (tau + 1 < tauMax && yinBuffer[tau + 1] < yinBuffer[tau]) {
                    tau++
                }
                break
            }
        }

        // If no dip below standard threshold, search for strict local minimum below subharmonic threshold
        if (tau == -1) {
            var bestMin = subharmonicThreshold
            for (t in tauMin until tauMax) {
                if (yinBuffer[t] < bestMin) {
                    // Check if it is a local minimum
                    val isLocalMin = (t > tauMin && t < tauMax - 1) &&
                            (yinBuffer[t] <= yinBuffer[t - 1] && yinBuffer[t] <= yinBuffer[t + 1])
                    if (isLocalMin) {
                        bestMin = yinBuffer[t]
                        tau = t
                    }
                }
            }
            if (tau == -1) return PitchResult(0f, 0f)
        }

        // Subharmonic Octave Guard: Check if double lag 2*tau (fundamental F0) is a valid local minimum
        val doubleTau = tau * 2
        if (doubleTau in tauMin until tauMax) {
            var checkTau = doubleTau
            while (checkTau + 1 < tauMax && yinBuffer[checkTau + 1] < yinBuffer[checkTau]) {
                checkTau++
            }
            if (yinBuffer[checkTau] < subharmonicThreshold) {
                tau = checkTau // Select double lag (fundamental F0)
            }
        }

        // Step 4: Parabolic Interpolation for sub-sample accuracy
        val betterTau = if (tau in 1 until windowHalf - 1) {
            val s0 = yinBuffer[tau - 1]
            val s1 = yinBuffer[tau]
            val s2 = yinBuffer[tau + 1]
            val denominator = 2f * (2f * s1 - s2 - s0)
            if (kotlin.math.abs(denominator) > 1e-6f) {
                tau + (s2 - s0) / denominator
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

    private fun calculateRms(samples: FloatArray): Float {
        var sum = 0f
        for (i in samples.indices) {
            val s = samples[i]
            sum += s * s
        }
        return sqrt(sum / samples.size)
    }

    private fun updateAnalysisWindow(samples: FloatArray) {
        if (samples.size >= ANALYSIS_WINDOW_SIZE) {
            // Take the latest ANALYSIS_WINDOW_SIZE samples
            val start = samples.size - ANALYSIS_WINDOW_SIZE
            samples.copyInto(analysisWindow, 0, start, samples.size)
        } else {
            // Shift existing samples left and append new samples
            val shift = samples.size
            analysisWindow.copyInto(analysisWindow, 0, shift, ANALYSIS_WINDOW_SIZE)
            samples.copyInto(analysisWindow, ANALYSIS_WINDOW_SIZE - shift, 0, shift)
        }
    }
}
