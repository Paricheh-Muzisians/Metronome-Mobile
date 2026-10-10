package com.paricheh.metronome.tuner.model

data class TunerResult(
    val noteInfo: NoteInfo,
    val centsDifference: Float,
    val confidence: Float,
)
