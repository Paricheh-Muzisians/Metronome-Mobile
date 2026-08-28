package com.paricheh.metronome.metronome.data

import com.paricheh.metronome.core.Note
import com.paricheh.metronome.core.TimeSignature

data class MetronomePreferences(
    val tempo: Int,
    val selectedTimeSignature: TimeSignature?,
    val selectedBarStructure: List<Note>?,
    val vibrationEnabled: Boolean,
    val hasSeenUnboarding: Boolean,
)