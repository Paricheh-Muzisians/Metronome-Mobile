package com.paricheh.metronome.tuner.ui.utils.temperament

import com.paricheh.metronome.tuner.model.MusicalNote

interface Temperament {
    fun getFrequency(note: MusicalNote, octave: Int): Float
}