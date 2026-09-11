package com.paricheh.metronome.rating.data.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class IosRatingPreferences : RatingPreferences {
    // TODO: Implement using NSUserDefaults following the pattern in IosMetronomeSettings.kt

    override val hasRated: Flow<Boolean> = MutableStateFlow(false)
    override val lastPromptTimeInMillis: Flow<Long?> = MutableStateFlow(null)
    override val ratePoint: Flow<Int> = MutableStateFlow(0)

    override suspend fun setHasRated(hasRated: Boolean) {
        // TODO
    }

    override suspend fun setLastPromptTime(timestamp: Long) {
        // TODO
    }

    override suspend fun setRatePoint(point: Int) {
        // TODO
    }
}
