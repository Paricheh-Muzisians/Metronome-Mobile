package com.paricheh.metronome.tuner.model

data class NoteInfo(
    val note: MusicalNote,
    val octave: Int,
    val frequency: Float,
)

fun NoteInfo?.isSameNote(other: NoteInfo?): Boolean {
    if ((this == null) || (other == null)) return false
    return (note == other.note) && (octave == other.octave)
}
