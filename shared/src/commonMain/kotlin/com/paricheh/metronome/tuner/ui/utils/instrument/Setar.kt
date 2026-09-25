package com.paricheh.metronome.tuner.ui.utils.instrument

import com.paricheh.metronome.tuner.model.MusicalNote
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.ui.utils.temperament.EqualTemperament
import com.paricheh.metronome.tuner.ui.utils.temperament.Temperament

class Setar(
    private val temperament: Temperament = EqualTemperament(),
) : Instrument {
    override val key: String = KEY
    override val notes: List<NoteInfo> by lazy {
        listOf(
            NoteInfo(
                note = MusicalNote.Sol,
                octave = 3,
                frequency = temperament.getFrequency(MusicalNote.Sol, 3)

            ),
            NoteInfo(
                note = MusicalNote.Do,
                octave = 4,
                frequency = temperament.getFrequency(MusicalNote.Do, 4)
            ),
            NoteInfo(
                note = MusicalNote.Do,
                octave = 4,
                frequency = temperament.getFrequency(MusicalNote.Do, 4)
            ),
            NoteInfo(
                note = MusicalNote.Do,
                octave = 4,
                frequency = temperament.getFrequency(MusicalNote.Sol, 4)
            ),
        )
    }

    companion object {
        const val KEY = "setar"
    }
}