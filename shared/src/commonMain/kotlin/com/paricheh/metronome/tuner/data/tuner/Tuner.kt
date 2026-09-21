package com.paricheh.metronome.tuner.data.tuner

import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.model.TunerResult

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
    fun process(
        frequency: Float,
        confidence: Float,
        targetNote: NoteInfo?,
        notes: List<NoteInfo>,
    ): TunerResult?
}
