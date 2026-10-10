package com.paricheh.metronome.tuner.ui.utils.instrument

import com.paricheh.metronome.tuner.model.MusicalNote
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.ui.utils.temperament.EqualTemperament
import com.paricheh.metronome.tuner.ui.utils.temperament.Temperament

class Guitar6String(
    private val temperament: Temperament = EqualTemperament(),
) : Instrument {
    override val key: String = KEY

    override val notes: List<NoteInfo> by lazy {
        // Standard open strings: E2, A2, D3, G3, B3, E4
        val openStringMidiNotes = listOf(40, 45, 50, 55, 59, 64)

        openStringMidiNotes.map { midi ->
            val noteIndex = midi % 12
            val octave = (midi / 12) - 1
            val note = MusicalNote.entries[noteIndex]
            NoteInfo(note, octave, temperament.getFrequency(note, octave))
        }
    }

    companion object {
        const val KEY = "guitar-standard-e-tuning"
    }
}

