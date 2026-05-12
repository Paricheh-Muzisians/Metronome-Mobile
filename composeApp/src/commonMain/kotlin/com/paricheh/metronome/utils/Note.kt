package com.paricheh.metronome.utils

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class Note(
    val weight: Int,
    val isAccent: Boolean = false,
    val isDot: Boolean = false,
) {
    init {
        require(weight in setOf(2, 4, 8, 16))
    }

    fun getUnitCharByUnit(): String {
        return buildString {
            when (weight) {
                2 -> append(MusicSymbols.HALF_NOTE)
                4 -> append(MusicSymbols.QUARTER_NOTE)
                8 -> append(MusicSymbols.EIGHT_NOTE)
                16 -> append(MusicSymbols.SIXTEENTH_NOTE)
            }
            if (isAccent) append(MusicSymbols.ACCENT)
            if (isDot) append(MusicSymbols.COMBINING_AUGMENTATION_DOT)
        }
    }
}
