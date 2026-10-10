package com.paricheh.metronome.tuner.ui.utils.instrument

import com.paricheh.metronome.tuner.model.MusicalNote
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.ui.utils.temperament.EqualTemperament
import com.paricheh.metronome.tuner.ui.utils.temperament.Temperament

sealed interface Setar : Instrument

sealed class BaseSetar(
    override val key: String,
    temperament: Temperament = EqualTemperament(),
    string1: NoteInfo = NoteInfo(
        note = MusicalNote.Do,
        octave = 4,
        frequency = temperament.getFrequency(MusicalNote.Do, 4)
    ),
    string2: NoteInfo = NoteInfo(
        note = MusicalNote.Sol,
        octave = 3,
        frequency = temperament.getFrequency(MusicalNote.Sol, 3)
    ),
    string3: NoteInfo,
    string4: NoteInfo,
) : Setar {
    override val notes: List<NoteInfo> = listOf(string1, string2, string3, string4)
}

class SetarMahoor(temperament: Temperament = EqualTemperament()) : BaseSetar(
    key = KEY,
    temperament = temperament,
    string3 = NoteInfo(
        note = MusicalNote.Do,
        octave = 4,
        frequency = temperament.getFrequency(MusicalNote.Do, 4)
    ),
    string4 = NoteInfo(
        note = MusicalNote.Do,
        octave = 3,
        frequency = temperament.getFrequency(MusicalNote.Do, 3)
    ),
) {
    companion object {
        const val KEY = "setar_mahoor"
    }
}

class SetarShur(temperament: Temperament = EqualTemperament()) : BaseSetar(
    key = KEY,
    temperament = temperament,
    string3 = NoteInfo(
        note = MusicalNote.Do,
        octave = 4,
        frequency = temperament.getFrequency(MusicalNote.Do, 4)
    ),
    string4 = NoteInfo(
        note = MusicalNote.Fa,
        octave = 3,
        frequency = temperament.getFrequency(MusicalNote.Fa, 3)
    ),
) {
    companion object {
        const val KEY = "setar_shur"
    }
}

class SetarHomayoun(temperament: Temperament = EqualTemperament()) : BaseSetar(
    key = KEY,
    temperament = temperament,
    string3 = NoteInfo(
        note = MusicalNote.Re,
        octave = 4,
        frequency = temperament.getFrequency(MusicalNote.Re, 4)
    ),
    string4 = NoteInfo(
        note = MusicalNote.Re,
        octave = 3,
        frequency = temperament.getFrequency(MusicalNote.Re, 3)
    ),
) {
    companion object {
        const val KEY = "setar_homayoun"
    }
}

class SetarChahargah(temperament: Temperament = EqualTemperament()) : BaseSetar(
    key = KEY,
    temperament = temperament,
    string3 = NoteInfo(
        note = MusicalNote.Do,
        octave = 4,
        frequency = temperament.getFrequency(MusicalNote.Do, 4)
    ),
    string4 = NoteInfo(
        note = MusicalNote.Do,
        octave = 3,
        frequency = temperament.getFrequency(MusicalNote.Do, 3)
    ),
) {
    companion object {
        const val KEY = "setar_chahargah"
    }
}

class SetarRastPanjgah(temperament: Temperament = EqualTemperament()) : BaseSetar(
    key = KEY,
    temperament = temperament,
    string3 = NoteInfo(
        note = MusicalNote.Do,
        octave = 4,
        frequency = temperament.getFrequency(MusicalNote.Do, 4)
    ),
    string4 = NoteInfo(
        note = MusicalNote.Do,
        octave = 3,
        frequency = temperament.getFrequency(MusicalNote.Do, 3)
    ),
) {
    companion object {
        const val KEY = "setar_rastpanjgah"
    }
}

class SetarNava(temperament: Temperament = EqualTemperament()) : BaseSetar(
    key = KEY,
    temperament = temperament,
    string3 = NoteInfo(
        note = MusicalNote.Re,
        octave = 4,
        frequency = temperament.getFrequency(MusicalNote.Re, 4)
    ),
    string4 = NoteInfo(
        note = MusicalNote.Re,
        octave = 3,
        frequency = temperament.getFrequency(MusicalNote.Re, 3)
    ),
) {
    companion object {
        const val KEY = "setar_nava"
    }
}

class SetarEsfahan(temperament: Temperament = EqualTemperament()) : BaseSetar(
    key = KEY,
    temperament = temperament,
    string3 = NoteInfo(
        note = MusicalNote.Re,
        octave = 4,
        frequency = temperament.getFrequency(MusicalNote.Re, 4)
    ),
    string4 = NoteInfo(
        note = MusicalNote.Re,
        octave = 3,
        frequency = temperament.getFrequency(MusicalNote.Re, 3)
    ),
) {
    companion object {
        const val KEY = "setar_esfahan"
    }
}

class SetarAbuata(temperament: Temperament = EqualTemperament()) : BaseSetar(
    key = KEY,
    temperament = temperament,
    string3 = NoteInfo(
        note = MusicalNote.Do,
        octave = 4,
        frequency = temperament.getFrequency(MusicalNote.Do, 4)
    ),
    string4 = NoteInfo(
        note = MusicalNote.Do,
        octave = 3,
        frequency = temperament.getFrequency(MusicalNote.Do, 3)
    ),
) {
    companion object {
        const val KEY = "setar_abuata"
    }
}

class SetarDashti(temperament: Temperament = EqualTemperament()) : BaseSetar(
    key = KEY,
    temperament = temperament,
    string3 = NoteInfo(
        note = MusicalNote.Re,
        octave = 4,
        frequency = temperament.getFrequency(MusicalNote.Re, 4)
    ),
    string4 = NoteInfo(
        note = MusicalNote.Re,
        octave = 3,
        frequency = temperament.getFrequency(MusicalNote.Re, 3)
    ),
) {
    companion object {
        const val KEY = "setar_dashti"
    }
}

class SetarBayatTork(temperament: Temperament = EqualTemperament()) : BaseSetar(
    key = KEY,
    temperament = temperament,
    string3 = NoteInfo(
        note = MusicalNote.Do,
        octave = 4,
        frequency = temperament.getFrequency(MusicalNote.Do, 4)
    ),
    string4 = NoteInfo(
        note = MusicalNote.Do,
        octave = 3,
        frequency = temperament.getFrequency(MusicalNote.Do, 3)
    ),
) {
    companion object {
        const val KEY = "setar_bayattork"
    }
}
