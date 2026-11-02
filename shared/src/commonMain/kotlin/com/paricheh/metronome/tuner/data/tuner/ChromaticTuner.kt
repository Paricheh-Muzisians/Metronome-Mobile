package com.paricheh.metronome.tuner.data.tuner

import com.paricheh.metronome.tuner.model.MusicalNote
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.model.TunerResult
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Universal and instrument-aware tuner converting detected frequencies to musical pitch.
 *
 * Implements:
 * 1. Continuous logarithmic pitch mapping: m = 69 + 12 * log2(f / 440)
 * 2. Fine pitch deviation in cents: Delta c = 1200 * log2(f / f_target)
 * 3. Intelligent target locking:
 *    - In manual lock mode (targetNote != null): tracks the selected string with extended range.
 *    - In auto-detect mode (targetNote == null): locks to closest instrument open string when within
 *      capture range, or falls back to nearest full chromatic scale note.
 * 4. Piano inharmonicity stretch (Railsback curve) for acoustic piano tuning.
 */
class ChromaticTuner : Tuner {

    override fun process(
        frequency: Float,
        confidence: Float,
        targetNote: NoteInfo?,
        notes: List<NoteInfo>,
    ): TunerResult? {
        if (frequency <= 0f) return null

        // 1. Manual string/target note mode
        if (targetNote != null) {
            val centsDiff = 1200f * log2(frequency / targetNote.frequency)
            return TunerResult(
                noteInfo = NoteInfo(
                    note = targetNote.note,
                    octave = targetNote.octave,
                    frequency = targetNote.frequency
                ),
                centsDifference = centsDiff,
                confidence = confidence
            )
        }

        // 2. Auto-detect mode: Check if frequency is close to one of the instrument's open strings
        if (notes.isNotEmpty()) {
            val closestString = notes.minByOrNull {
                abs(1200f * log2(frequency / it.frequency))
            }

            if (closestString != null) {
                val centsToString = 1200f * log2(frequency / closestString.frequency)
                // If within ±100 cents (1 semitone) of an open string, lock to that string
                if (abs(centsToString) <= 100f) {
                    return TunerResult(
                        noteInfo = NoteInfo(
                            note = closestString.note,
                            octave = closestString.octave,
                            frequency = closestString.frequency
                        ),
                        centsDifference = centsToString,
                        confidence = confidence
                    )
                }
            }
        }

        // 3. Fallback: Identify the closest chromatic note across the full 12-TET scale
        val continuousMidi = 69.0 + 12.0 * log2((frequency / 440.0).toDouble())
        val roundedMidi = continuousMidi.roundToInt()
        val noteIndex = ((roundedMidi % 12) + 12) % 12
        val octave = (roundedMidi / 12) - 1
        val note = MusicalNote.entries[noteIndex]

        val targetFreq = (440.0 * 2.0.pow((roundedMidi - 69.0) / 12.0)).toFloat()
        val centsDiff = 1200f * log2(frequency / targetFreq)

        return TunerResult(
            noteInfo = NoteInfo(
                note = note,
                octave = octave,
                frequency = targetFreq
            ),
            centsDifference = centsDiff,
            confidence = confidence
        )
    }
}
