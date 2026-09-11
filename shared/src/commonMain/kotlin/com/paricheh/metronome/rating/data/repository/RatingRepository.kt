package com.paricheh.metronome.rating.data.repository

import kotlinx.coroutines.flow.Flow

interface RatingRepository {
    fun observeShouldShowRating(): Flow<Boolean>
    suspend fun markAsPrompt()
    suspend fun markAsRated()
    suspend fun increaseRatingPoint()
}