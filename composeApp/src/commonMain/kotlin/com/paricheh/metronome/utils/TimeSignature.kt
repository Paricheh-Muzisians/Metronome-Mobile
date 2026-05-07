package com.paricheh.metronome.utils

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.paricheh.metronome.utils.TimeSignatureType.Compound
import com.paricheh.metronome.utils.TimeSignatureType.Irregular
import com.paricheh.metronome.utils.TimeSignatureType.Simple

@Stable
class TimeSignature(
    val numerator: Int,
    val denominator: Int,
) {
    val type = calculateType()
    val defaultBarsStructure = calculateBarsStructure()
    var convertedBarsStructure by mutableStateOf(defaultBarsStructure)

    infix fun convertDenominator(to: Int) {
        defaultBarsStructure
    }

    fun getUnitNote() = when (type) {
        Simple -> {
            Note(weight = denominator)
        }

        else -> {
            Note(
                weight = denominator / 2,
                isDot = true
            )
        }
    }

    private fun calculateBarsStructure(): List<Note> {
        return when (type) {
            Simple -> {
                List(numerator) {
                    Note(
                        weight = denominator,
                        isAccent = it == 0
                    )
                }
            }

            Compound -> {
                List(numerator / 3) {
                    Note(
                        weight = denominator / 2,
                        isAccent = it == 0,
                        isDot = true
                    )
                }
            }

            Irregular -> {
                List(numerator / 3) {
                    Note(
                        weight = denominator / 2,
                        isAccent = it == 0,
                        isDot = it == numerator / 3 - 1
                    )
                }
            }
        }
    }

    private fun calculateType(): TimeSignatureType {
        if (denominator == 8 && numerator % 3 == 0 && numerator > 3) {
            return Compound
        }

        val simpleDenominators = setOf(2, 4, 8)
        val simpleNumerators = setOf(2, 3, 4)

        if (denominator in simpleDenominators && numerator in simpleNumerators) {
            return Simple
        }

        return Irregular
    }

    fun isCommon() =
        numerator to denominator in commonTimeSignatures

    companion object {
        val commonTimeSignatures = setOf(
            2 to 4,
            3 to 4,
            4 to 4,
            5 to 4,
            6 to 8,
            7 to 8,
            9 to 8,
            12 to 8
        )
    }
}

enum class TimeSignatureType {
    Simple,
    Compound,
    Irregular
}