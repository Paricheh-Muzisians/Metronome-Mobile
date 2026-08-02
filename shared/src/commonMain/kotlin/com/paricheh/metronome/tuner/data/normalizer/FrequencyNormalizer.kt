package com.paricheh.metronome.tuner.data.normalizer

/**
 * Interface for frequency smoothing and jitter reduction.
 */
interface FrequencyNormalizer {
    /**
     * Normalizes/smooths the detected frequency.
     *
     * @param frequency The raw detected frequency.
     * @param confidence The detection confidence.
     * @return The normalized frequency.
     */
    fun normalize(frequency: Float, confidence: Float): Float

    /**
     * Resets the normalizer state.
     */
    fun reset()
}
