package com.paricheh.metronome.tuner.data.normalizer

/**
 * Basic implementation of frequency smoothing using a simple moving average or EMA.
 *
 * For Phase 1, we use an Exponential Moving Average (EMA) to reduce jitter.
 */
class SimpleFrequencyNormalizer(
    private val alpha: Float = 0.3f // Smoothing factor
) : FrequencyNormalizer {
    private var lastFrequency: Float = 0f

    override fun normalize(frequency: Float, confidence: Float): Float {
        if (frequency <= 0f || confidence < 0.5f) {
            lastFrequency = 0f
            return 0f
        }

        lastFrequency = if (lastFrequency == 0f) {
            frequency
        } else {
            // EMA: y = alpha * x + (1 - alpha) * y_prev
            alpha * frequency + (1f - alpha) * lastFrequency
        }

        return lastFrequency
    }

    override fun reset() {
        lastFrequency = 0f
    }
}
