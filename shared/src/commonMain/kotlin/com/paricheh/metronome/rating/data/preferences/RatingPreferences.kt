package com.paricheh.metronome.rating.data.preferences

import kotlinx.coroutines.flow.Flow

interface RatingPreferences {
    val hasRated: Flow<Boolean>
    val lastPromptTimeInMillis: Flow<Long?>
    val ratePoint: Flow<Int>

    suspend fun setHasRated(hasRated: Boolean)
    suspend fun setLastPromptTime(timestamp: Long)
    suspend fun setRatePoint(point: Int)
}