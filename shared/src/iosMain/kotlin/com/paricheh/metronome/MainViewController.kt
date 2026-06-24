package com.paricheh.metronome

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(
            // Add New modules here
        )
    }

    App()
}