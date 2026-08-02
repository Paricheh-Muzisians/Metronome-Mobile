package com.paricheh.metronome.tuner.data.detector

import com.paricheh.metronome.core.audio.AudioFrame

/**
 * Result of pitch detection.
 *
 * @property frequency The detected frequency in Hz. 0.0 if no pitch detected.
 * @property confidence A value between 0.0 and 1.0 representing the confidence of detection.
 */
data class PitchResult(
    val frequency: Float,
    val confidence: Float
)

/**
 * Interface for pitch detection algorithms.
 */
interface PitchDetector {
    /**
     * Detects the pitch from the given [AudioFrame].
     */
    fun detect(frame: AudioFrame): PitchResult
}
