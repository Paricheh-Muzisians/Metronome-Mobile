package com.paricheh.metronome.tuner.data.tuner

import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.model.TunerResult
import kotlin.math.abs
import kotlin.math.log2

class ChromaticTuner : Tuner {

    override fun process(
        frequency: Float,
        confidence: Float,
        targetNote: NoteInfo?,
        notes: List<NoteInfo>,
    ): TunerResult? {
        if (frequency <= 0f) return null

        val note = targetNote
            ?: notes.minByOrNull { abs(log2(it.frequency / frequency)) }
            ?: return null

        val centsDifference = 1200f * log2(frequency / note.frequency)

        if (centsDifference < -150f || centsDifference > 150f) {
            return null
        }

        return TunerResult(
            noteInfo = NoteInfo(
                frequency = frequency,
                note = note.note,
                octave = note.octave,
            ),
            centsDifference = centsDifference,
            confidence = confidence
        )
    }
}
