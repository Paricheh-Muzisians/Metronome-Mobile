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
        require(weight in validNoteWeights) {
            "weight $weight is not supported"
        }
    }

    fun getUnitCharByUnit(): String {
        return buildString {
            when (weight) {
                1 -> append(MusicSymbols.WHOLE_NOTE)
                2 -> append(MusicSymbols.HALF_NOTE)
                4 -> append(MusicSymbols.QUARTER_NOTE)
                8 -> append(MusicSymbols.EIGHT_NOTE)
                16 -> append(MusicSymbols.SIXTEENTH_NOTE)
                32 -> append(MusicSymbols.THIRTY_SECOND_NOTE)
                64 -> append(MusicSymbols.SIXTY_FOURTH_NOTE)
                128 -> append(MusicSymbols.ONE_HUNDRED_TWENTY_EIGHT_NOTE)
            }
            if (isAccent) append(MusicSymbols.ACCENT)
            if (isDot) append(MusicSymbols.COMBINING_AUGMENTATION_DOT)
        }
    }

    companion object {
        val validNoteWeights = setOf(1, 2, 4, 8, 16, 32, 64, 128)
        val timeSignatureUnits = setOf(2, 4, 8, 16)
    }
}
