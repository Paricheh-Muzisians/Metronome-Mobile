package com.paricheh.metronome.data

data class MetronomePreferences(
    val tempo: Int = 120,
    val timeSignatureBeats: Int = 4,
    val timeSignatureBeatUnit: Int = 4,
    val accentFirstBeat: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = false
)
