package com.paricheh.metronome.tuner.data.tuner

import com.paricheh.metronome.tuner.data.theory.Instrument
import com.paricheh.metronome.tuner.data.theory.NoteInfo
import kotlin.math.abs
import kotlin.math.log2

/**
 * Implementation of [Tuner] that finds the nearest note on a given [Instrument].
 */
class ChromaticTuner(
    private val instrument: Instrument,
) : Tuner {

    override fun process(
        frequency: Float,
        confidence: Float,
        targetNote: NoteInfo?,
    ): TunerResult? {
        if (frequency <= 0f || confidence < 0.7) return null

        val note = targetNote
            ?: instrument.notes.minByOrNull { abs(it.frequency - frequency) }
            ?: return null

        // Calculate cents difference: cents = 1200 * log2(f1 / f2)
        val centsDifference = 1200f * log2(frequency / note.frequency)

        return TunerResult(
            frequency = frequency,
            note = note.note,
            octave = note.octave,
            centsDifference = centsDifference,
            confidence = confidence
        )
    }
}
