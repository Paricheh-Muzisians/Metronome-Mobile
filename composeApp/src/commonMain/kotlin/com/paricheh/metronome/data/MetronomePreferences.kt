package com.paricheh.metronome.data

import com.paricheh.metronome.utils.Note
import com.paricheh.metronome.utils.TimeSignature

data class MetronomePreferences(
    val tempo: Int,
    val selectedTimeSignature: TimeSignature?,
    val selectedBarStructure: List<Note>?,
    val vibrationEnabled: Boolean,
)
