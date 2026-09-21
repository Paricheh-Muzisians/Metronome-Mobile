package com.paricheh.metronome.tuner.model

data class NoteInfo(
    val note: MusicalNote,
    val octave: Int,
    val frequency: Float,
)