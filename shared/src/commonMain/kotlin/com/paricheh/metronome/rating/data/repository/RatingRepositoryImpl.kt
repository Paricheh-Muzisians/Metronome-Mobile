package com.paricheh.metronome.rating.data.repository

import com.paricheh.metronome.core.getCurrentTimeMillis
import com.paricheh.metronome.rating.data.preferences.RatingPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.days

class RatingRepositoryImpl(
    private val ratingPreferences: RatingPreferences,
) : RatingRepository {

    override fun observeShouldShowRating(): Flow<Boolean> {
        return combine(
            flow = ratingPreferences.hasRated,
            flow2 = ratingPreferences.ratePoint,
            flow3 = ratingPreferences.lastPromptTimeInMillis
        ) { hasRated, point, lastPrompt ->
            if (!hasRated && point >= 5) {
                if (lastPrompt != null) {
                    getCurrentTimeMillis() - lastPrompt >= 7.days.inWholeMilliseconds
                } else {
                    true
                }
            } else {
                false
            }
        }
    }

    override suspend fun markAsPrompt() {
        ratingPreferences.setLastPromptTime(getCurrentTimeMillis())
        ratingPreferences.setRatePoint(0)
    }

    override suspend fun markAsRated() {
        ratingPreferences.setHasRated(true)
    }

    override suspend fun increaseRatingPoint() {
        val currentRate = ratingPreferences.ratePoint
            .first()
        ratingPreferences.setRatePoint(currentRate + 1)
    }
}