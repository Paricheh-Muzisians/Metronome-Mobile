// commonMain/kotlin/com/yourapp/metronome/data/MetronomePreferences.kt
package com.paricheh.metronome.settings

data class MetronomePreferences(
    val tempo: Int = 120,
    val beatsPerMeasure: Int = 4,
    val subdivisions: Int = 1,
    val soundEnabled: Boolean = true,
    val visualEnabled: Boolean = true,
    val accentFirstBeat: Boolean = true
)
