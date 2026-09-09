package com.paricheh.metronome.core.analytics

internal class StubAnalyticsManager : AnalyticsManager {
    override fun track(event: String, parameters: Map<String, Any?>) {
        println("Analytics event:")
        println("name = $event")
        if (parameters.isNotEmpty()) {
            println("parameters = $parameters")
        }
    }
}
