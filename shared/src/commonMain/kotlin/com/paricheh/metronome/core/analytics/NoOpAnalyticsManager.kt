package com.paricheh.metronome.core.analytics

internal class NoOpAnalyticsManager : AnalyticsManager {
    override fun track(
        event: String,
        parameters: Map<String, Any?>
    ) = Unit
}
