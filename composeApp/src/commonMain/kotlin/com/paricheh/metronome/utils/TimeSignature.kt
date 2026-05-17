package com.paricheh.metronome.utils

import androidx.compose.runtime.Stable
import com.paricheh.metronome.utils.TimeSignatureType.Compound
import com.paricheh.metronome.utils.TimeSignatureType.Irregular
import com.paricheh.metronome.utils.TimeSignatureType.Simple
import kotlinx.serialization.Serializable

@Stable
@Serializable
class TimeSignature(
    val numerator: Int,
    val denominator: Int,
) {
    val type = calculateType()
    val defaultBarsStructure = calculateBarsStructure()

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
                var numberOfDotedNote = numerator % denominator

                List((numerator / 3) + 1) {
                    Note(
                        weight = denominator / 2,
                        isAccent = it == numerator / 3,
                        isDot = numberOfDotedNote-- > 0
                    )
                }.reversed()
            }
        }
    }

    private fun calculateType(): TimeSignatureType {
        if (denominator % 2 == 0 && numerator % 3 == 0) {
            return Compound
        }

        val simpleDenominators = setOf(2, 4, 8)
        val simpleNumerators = setOf(2, 3, 4)

        if (denominator in simpleDenominators && numerator in simpleNumerators) {
            return Simple
        }

        return Irregular
    }

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