package com.paricheh.metronome.tuner.data.tuner

import com.paricheh.metronome.tuner.data.theory.MusicalNote
import com.paricheh.metronome.tuner.data.theory.TuningStatus

/**
 * Domain model representing the detailed state of a detected pitch.
 */
data class TunerResult(
    val frequency: Float,
    val note: MusicalNote,
    val octave: Int,
    val centsDifference: Float,
    val tuningStatus: TuningStatus,
    val confidence: Float
)

/**
 * Interface for converting frequency to musical information.
 */
interface Tuner {
    /**
     * Processes a normalized frequency and returns musical information.
     *
     * @param frequency The detected and normalized frequency.
     * @param confidence The detection confidence.
     * @return [TunerResult] if a note is detected, null otherwise.
     */
    fun process(frequency: Float, confidence: Float): TunerResult?
}
