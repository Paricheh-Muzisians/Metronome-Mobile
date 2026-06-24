package com.paricheh.metronome.core

import androidx.compose.runtime.Stable
import com.paricheh.metronome.core.TimeSignatureType.Compound
import com.paricheh.metronome.core.TimeSignatureType.Irregular
import com.paricheh.metronome.core.TimeSignatureType.Simple
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
                var numberOfDotedNote = 0

                List(numerator) {
                    Note(denominator)
                }.chunked(2)
                    .mapIndexed { index, notes ->
                        if (notes.size == 1) {
                            numberOfDotedNote++
                        }

                        Note(
                            weight = denominator / 2,
                            isAccent = index == 0
                        )
                    }
                    .toMutableList()
                    .apply {
                        repeat(numberOfDotedNote) {
                            removeLast()
                            removeLast()
                        }

                        repeat(numberOfDotedNote) {
                            add(
                                first().copy(
                                    isDot = true,
                                    isAccent = false
                                )
                            )
                        }
                    }
            }
        }
    }

    private fun calculateType(): TimeSignatureType {
        val simpleDenominators = setOf(2, 4, 8)
        val simpleNumerators = setOf(1, 2, 3, 4)

        if (denominator in simpleDenominators && numerator in simpleNumerators) {
            return Simple
        }

        if (denominator % 2 == 0 && numerator % 3 == 0) {
            return Compound
        }

        return Irregular
    }

    companion object {
        val commonTimeSignatures = setOf(
            2 to 4,
            3 to 4,
            4 to 4,
            6 to 8,
            9 to 8,
            2 to 2,
            3 to 8,
            12 to 8
        )
    }
}

enum class TimeSignatureType {
    Simple,
    Compound,
    Irregular
}
