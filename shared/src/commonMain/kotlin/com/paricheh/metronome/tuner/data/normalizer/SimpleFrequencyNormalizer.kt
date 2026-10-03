package com.paricheh.metronome.tuner.data.normalizer

import kotlin.math.abs
import kotlin.math.log2
import kotlin.time.Clock

/**
 * Robust frequency normalizer utilizing Median Filtering and Hysteresis Hold Duration.
 *
 * Replaces linear EMA frequency blending with:
 * 1. Median filter (window size N=5) to reject single-frame octave spikes and glitches.
 * 2. Release hold duration (150ms) to prevent needle flickering during momentary amplitude drops.
 * 3. Confidence thresholding tailored to the pitch detector quality gate.
 */
class SimpleFrequencyNormalizer(
    private val windowSize: Int = 5,
    private val minConfidence: Float = 0.55f,
    private val holdDurationMs: Long = 150L
) : FrequencyNormalizer {

    private val history = FloatArray(windowSize)
    private var historyCount = 0
    private var historyHead = 0
    private var lastValidTimestamp: Long = 0L
    private var lastEmittedFrequency: Float = 0f

    override fun normalize(frequency: Float, confidence: Float): Float {
        val currentTime = currentTimeMillis()

        if (frequency <= 0f || confidence < minConfidence) {
            // Signal drop / low confidence: apply hold duration hysteresis
            return if (lastValidTimestamp > 0L && (currentTime - lastValidTimestamp) < holdDurationMs) {
                lastEmittedFrequency
            } else {
                reset()
                0f
            }
        }

        // Check for octave jump relative to current median
        val currentMedian = calculateMedian()
        if (currentMedian > 0f && isOctaveJump(frequency, currentMedian)) {
            // Momentary octave jump spike: hold current median instead of adding octave jump to history
            lastValidTimestamp = currentTime
            return currentMedian
        }

        // Add valid frequency to circular buffer
        addSample(frequency)
        lastValidTimestamp = currentTime

        val filteredFreq = calculateMedian()
        lastEmittedFrequency = filteredFreq
        return filteredFreq
    }

    override fun reset() {
        historyCount = 0
        historyHead = 0
        lastValidTimestamp = 0L
        lastEmittedFrequency = 0f
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

    private fun isOctaveJump(freq1: Float, freq2: Float): Boolean {
        if (freq1 <= 0f || freq2 <= 0f) return false
        val ratio = freq1 / freq2
        val semitones = abs(12f * log2(ratio))
        // Check if semitones distance is near 12 (octave high) or -12 (octave low) within +/- 1.5 semitones
        return abs(semitones - 12f) < 1.5f || abs(semitones - 24f) < 1.5f
    }

    private fun currentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}
