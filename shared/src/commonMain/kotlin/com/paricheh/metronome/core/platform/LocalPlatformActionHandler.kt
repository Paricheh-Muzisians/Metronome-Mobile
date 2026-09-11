package com.paricheh.metronome.core.platform

import androidx.compose.runtime.staticCompositionLocalOf

val LocalPlatformActionHandler = staticCompositionLocalOf<PlatformActionHandler> {
    object : PlatformActionHandler {
        override fun openRatingPage() {}
        override fun showToast(text: String) {}
    }
}
