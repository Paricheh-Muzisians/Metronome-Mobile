package com.paricheh.metronome.tuner.model

data class TunerResult(
    val frequency: Float,
    val note: MusicalNote,
    val octave: Int,
    val centsDifference: Float,
    val confidence: Float,
)
