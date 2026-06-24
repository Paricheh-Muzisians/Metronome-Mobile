package com.paricheh.metronome.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class MetronomeScreens {

    @Serializable
    data object Metronome : MetronomeScreens()

    @Serializable
    data object Setting : MetronomeScreens()
}
