package com.paricheh.metronome.core.analytics

interface AnalyticsManager {
    fun track(
        event: String,
        parameters: Map<String, Any?> = emptyMap()
    )
}
