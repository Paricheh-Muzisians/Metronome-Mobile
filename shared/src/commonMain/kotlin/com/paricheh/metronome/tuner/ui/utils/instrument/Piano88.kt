package com.paricheh.metronome.tuner.ui.utils.instrument

import com.paricheh.metronome.tuner.model.MusicalNote
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.ui.utils.temperament.EqualTemperament
import com.paricheh.metronome.tuner.ui.utils.temperament.Temperament

class Piano88(
    private val temperament: Temperament = EqualTemperament(),
) : Instrument {
    override val key: String = KEY

    override val notes: List<NoteInfo> by lazy {
        val allNotes = mutableListOf<NoteInfo>()
        // Piano 88 keys: A0 (21) to C8 (108)
        for (midi in 21..108) {
            val noteIndex = midi % 12
            val octave = (midi / 12) - 1
            val note = MusicalNote.entries[noteIndex]
            allNotes.add(NoteInfo(note, octave, temperament.getFrequency(note, octave)))
        }
        allNotes
    }

    companion object {
        const val KEY = "piano-88-key"
    }
}
