package com.paricheh.metronome.core.analytics

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAnalyticsManager = staticCompositionLocalOf<AnalyticsManager> {
    NoOpAnalyticsManager()
}
