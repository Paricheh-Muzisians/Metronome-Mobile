package com.paricheh.metronome.tuner

import com.paricheh.metronome.tuner.model.MusicalNote
import com.paricheh.metronome.tuner.ui.utils.instrument.Setar
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarAbuata
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarBayatTork
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarChahargah
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarDashti
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarEsfahan
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarHomayoun
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarMahoor
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarNava
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarRastPanjgah
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarShur
import kotlin.test.Test
import kotlin.test.assertEquals

class SetarInstrumentsTest {

    @Test
    fun testAllTenSetarTunings() {
        val mahoor = SetarMahoor()
        val shur = SetarShur()
        val homayoun = SetarHomayoun()
        val chahargah = SetarChahargah()
        val rastPanjgah = SetarRastPanjgah()
        val nava = SetarNava()
        val esfahan = SetarEsfahan()
        val abuata = SetarAbuata()
        val dashti = SetarDashti()
        val bayatTork = SetarBayatTork()

        val allSetars: List<Setar> = listOf(
            mahoor, shur, homayoun, chahargah,
            rastPanjgah, nava, esfahan, abuata, dashti, bayatTork
        )

        // Verify count
        assertEquals(10, allSetars.size, "Should have 10 Setar tuning types")

        // Verify all have 4 strings
        allSetars.forEach { setar ->
            assertEquals(4, setar.notes.size, "${setar.key} must have exactly 4 strings")
            // 1st string: C4 (Do4), 2nd string: G3 (Sol3)
            assertEquals(MusicalNote.Do, setar.notes[0].note, "${setar.key} string 1 must be Do")
            assertEquals(4, setar.notes[0].octave, "${setar.key} string 1 octave must be 4")
            assertEquals(MusicalNote.Sol, setar.notes[1].note, "${setar.key} string 2 must be Sol")
            assertEquals(3, setar.notes[1].octave, "${setar.key} string 2 octave must be 3")
        }

        // Verify English Tag Keys
        assertEquals("setar_mahoor", mahoor.key)
        assertEquals("setar_shur", shur.key)
        assertEquals("setar_homayoun", homayoun.key)
        assertEquals("setar_chahargah", chahargah.key)
        assertEquals("setar_rastpanjgah", rastPanjgah.key)
        assertEquals("setar_nava", nava.key)
        assertEquals("setar_esfahan", esfahan.key)
        assertEquals("setar_abuata", abuata.key)
        assertEquals("setar_dashti", dashti.key)
        assertEquals("setar_bayattork", bayatTork.key)
    }
}
