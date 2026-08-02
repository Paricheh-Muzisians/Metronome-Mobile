package com.paricheh.metronome.tuner.data.tuner

import com.paricheh.metronome.tuner.data.theory.Instrument
import com.paricheh.metronome.tuner.data.theory.TuningStatus
import kotlin.math.abs
import kotlin.math.log2

/**
 * Implementation of [Tuner] that finds the nearest note on a given [Instrument].
 */
class ChromaticTuner(
    private val instrument: Instrument,
    private val perfectThreshold: Float = 5.0f, // cents
    private val warningThreshold: Float = 15.0f // cents
) : Tuner {

    override fun process(frequency: Float, confidence: Float): TunerResult? {
        if (frequency <= 0f) return null

        val nearestNote = instrument.notes.minByOrNull { abs(it.frequency - frequency) } ?: return null

        // Calculate cents difference: cents = 1200 * log2(f1 / f2)
        val centsDifference = 1200f * log2(frequency / nearestNote.frequency)

        val status = when {
            abs(centsDifference) <= perfectThreshold -> TuningStatus.Perfect
            abs(centsDifference) <= warningThreshold -> TuningStatus.Warning
            else -> TuningStatus.Bad
        }

        return TunerResult(
            frequency = frequency,
            note = nearestNote.note,
            octave = nearestNote.octave,
            centsDifference = centsDifference,
            tuningStatus = status,
            confidence = confidence
        )
    }
}
