package com.paricheh.metronome.tuner.data.normalizer

import kotlin.math.abs
import kotlin.math.log2
import kotlin.time.Clock

/**
 * Robust frequency normalizer combining Median Filtering, Adaptive EMA Smoothing,
 * and Hysteresis Hold Duration.
 *
 * Implements:
 * 1. 3-tap median filter to strip isolated single-frame pitch spikes without introducing lag.
 * 2. Adaptive Exponential Moving Average (EMA) smoother:
 *    - Snappy attack (alpha = 1.0) on new note plucks / large transitions.
 *    - Ultra-smooth tracking (alpha = 0.25) during note sustain to eliminate needle jitter.
 * 3. Hysteresis hold duration (150ms) to prevent visual flickering during momentary amplitude dips.
 */
class SimpleFrequencyNormalizer(
    private val windowSize: Int = 3,
    private val minConfidence: Float = 0.45f,
    private val holdDurationMs: Long = 150L
) : FrequencyNormalizer {

    private val history = FloatArray(windowSize)
    private var historyCount = 0
    private var historyHead = 0
    private var lastValidTimestamp: Long = 0L
    private var smoothedFrequency: Float = 0f

    override fun normalize(frequency: Float, confidence: Float): Float {
        val currentTime = currentTimeMillis()

        if (frequency <= 0f || confidence < minConfidence) {
            // Signal drop / low confidence: apply hold duration hysteresis
            return if (lastValidTimestamp > 0L && (currentTime - lastValidTimestamp) < holdDurationMs) {
                smoothedFrequency
            } else {
                reset()
                0f
            }
        }

        // Add valid frequency to circular buffer for median filtering
        addSample(frequency)
        lastValidTimestamp = currentTime

        val medianFreq = calculateMedian()

        // Adaptive EMA Smoothing:
        if (smoothedFrequency <= 0f) {
            smoothedFrequency = medianFreq
        } else {
            val centsDiff = abs(1200f * log2(medianFreq / smoothedFrequency))
            val alpha = if (centsDiff > 50f) {
                // New note / pluck attack: instantaneous snap
                1.0f
            } else {
                // Sustain: smooth smoothing for rock-solid visual needle
                0.25f
            }
            smoothedFrequency = alpha * medianFreq + (1f - alpha) * smoothedFrequency
        }

        return smoothedFrequency
    }

    override fun reset() {
        historyCount = 0
        historyHead = 0
        lastValidTimestamp = 0L
        smoothedFrequency = 0f
    }

    private fun addSample(value: Float) {
        history[historyHead] = value
        historyHead = (historyHead + 1) % windowSize
        if (historyCount < windowSize) {
            historyCount++
        }
    }

    private fun calculateMedian(): Float {
        if (historyCount == 0) return 0f
        if (historyCount == 1) return history[0]

        val temp = FloatArray(historyCount)
        for (i in 0 until historyCount) {
            temp[i] = history[i]
        }
        temp.sort()

        return if (historyCount % 2 == 1) {
            temp[historyCount / 2]
        } else {
            (temp[historyCount / 2 - 1] + temp[historyCount / 2]) / 2f
        }
    }

    private fun currentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}
