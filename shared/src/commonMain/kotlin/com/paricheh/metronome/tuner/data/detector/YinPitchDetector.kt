package com.paricheh.metronome.tuner.data.detector

import com.paricheh.metronome.core.audio.AudioFrame
import kotlin.math.min

/**
 * Implementation of the YIN algorithm for pitch detection.
 *
 * YIN is a time-domain pitch detection algorithm based on the autocorrelation function.
 * Reference: De Cheveigné, A., & Kawahara, H. (2002). YIN, a fundamental frequency
 * estimator for speech and music. The Journal of the Acoustical Society of America.
 */
class YinPitchDetector(
    private val threshold: Float = 0.10f // Standard YIN threshold
) : PitchDetector {

    override fun detect(frame: AudioFrame): PitchResult {
        val samples = frame.samples
        val sampleRate = frame.sampleRate
        val size = samples.size
        val halfSize = size / 2

        // Step 1: Difference Function
        val yinBuffer = FloatArray(halfSize)
        for (tau in 0 until halfSize) {
            for (i in 0 until halfSize) {
                val delta = samples[i] - samples[i + tau]
                yinBuffer[tau] += delta * delta
            }
        }

        // Step 2: Cumulative Mean Normalized Difference Function
        yinBuffer[0] = 1f
        var runningSum = 0f
        for (tau in 1 until halfSize) {
            runningSum += yinBuffer[tau]
            yinBuffer[tau] *= tau / runningSum
        }

        // Step 3: Absolute Threshold
        var tau = -1
        for (t in 1 until halfSize) {
            if (yinBuffer[t] < threshold) {
                tau = t
                // Find the first local minimum below the threshold
                while (tau + 1 < halfSize && yinBuffer[tau + 1] < yinBuffer[tau]) {
                    tau++
                }
                break
            }
        }

        // If no value below threshold, find the global minimum
        if (tau == -1) {
            var minVal = Float.MAX_VALUE
            for (t in 1 until halfSize) {
                if (yinBuffer[t] < minVal) {
                    minVal = yinBuffer[t]
                    tau = t
                }
            }
            // If global minimum is not good enough, return 0 frequency
            if (minVal > 0.4f) return PitchResult(0f, 0f)
        }

        // Step 4: Parabolic Interpolation for better accuracy
        val betterTau = if (tau > 0 && tau < halfSize - 1) {
            val s0 = yinBuffer[tau - 1]
            val s1 = yinBuffer[tau]
            val s2 = yinBuffer[tau + 1]
            tau + (s2 - s0) / (2 * (2 * s1 - s2 - s0))
        } else {
            tau.toFloat()
        }

        val frequency = sampleRate / betterTau
        val confidence = 1.0f - min(1.0f, yinBuffer[tau])

        return PitchResult(frequency, confidence)
    }
}
