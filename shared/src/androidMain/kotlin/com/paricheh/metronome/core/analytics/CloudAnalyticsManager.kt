package com.paricheh.metronome.core.analytics

import io.appmetrica.analytics.AppMetrica

internal class CloudAnalyticsManager : AnalyticsManager {
    override fun track(event: String, parameters: Map<String, Any?>) {
        AppMetrica.reportEvent(event, parameters)
    }
}
