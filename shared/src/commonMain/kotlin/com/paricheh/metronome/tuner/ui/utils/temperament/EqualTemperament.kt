package com.paricheh.metronome.tuner.ui.utils.temperament

import com.paricheh.metronome.tuner.model.MusicalNote
import kotlin.math.pow

class EqualTemperament : Temperament {
    override fun getFrequency(note: MusicalNote, octave: Int): Float {
        // A4 = 440Hz
        // Frequency = 440 * 2^((n - 57) / 12) where n is the MIDI note number
        // MIDI note 69 is A4 (La4)
        val midiNote = getMidiNote(note, octave)
        return (440.0 * 2.0.pow((midiNote - 69.0) / 12.0)).toFloat()
    }

    private fun getMidiNote(note: MusicalNote, octave: Int): Int {
        val noteIndex = note.ordinal
        // Standard convention: C4 is MIDI 60.
        // Do is index 0. 0 + (4+1)*12 = 60.
        return noteIndex + (octave + 1) * 12
    }
}
