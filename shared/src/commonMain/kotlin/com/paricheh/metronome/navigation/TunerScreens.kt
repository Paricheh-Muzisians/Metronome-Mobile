package com.paricheh.metronome.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
sealed class TunerScreens {

    @Serializable
    data object Tuner : TunerScreens()

    @Serializable
    data object Setting : TunerScreens()
}