package com.paricheh.metronome.tuner.ui.utils.instrument

import com.paricheh.metronome.tuner.model.MusicalNote
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.ui.utils.temperament.EqualTemperament
import com.paricheh.metronome.tuner.ui.utils.temperament.Temperament

class SetarShurDo(
    private val temperament: Temperament = EqualTemperament(),
) : Instrument {

    override val key: String = KEY

    override val notes: List<NoteInfo> by lazy {
        listOf(
            NoteInfo(
                note = MusicalNote.Do,
                octave = 4,
                frequency = temperament.getFrequency(MusicalNote.Do, 4)
            ),
            NoteInfo(
                note = MusicalNote.Fa,
                octave = 3,
                frequency = temperament.getFrequency(MusicalNote.Fa, 3)
            ),
            NoteInfo(
                note = MusicalNote.Do,
                octave = 4,
                frequency = temperament.getFrequency(MusicalNote.Do, 4)
            ),
            NoteInfo(
                note = MusicalNote.Do,
                octave = 3,
                frequency = temperament.getFrequency(MusicalNote.Do, 3)
            ),
        )
    }

    companion object {
        const val KEY = "setar-shur-do"
    }
}