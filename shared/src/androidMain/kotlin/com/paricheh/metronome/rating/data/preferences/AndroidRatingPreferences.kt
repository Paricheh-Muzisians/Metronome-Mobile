package com.paricheh.metronome.rating.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.ratingDataStore: DataStore<Preferences> by preferencesDataStore(name = "rating_preferences")

internal class AndroidRatingPreferences(
    context: Context,
) : RatingPreferences {

    private val dataStore = context.ratingDataStore

    private object Keys {
        val HAS_RATED = booleanPreferencesKey("has_rated")
        val LAST_PROMPT_TIME = longPreferencesKey("last_prompt_time")
        val RATE_POINT = intPreferencesKey("rate_point")
    }

    override val hasRated: Flow<Boolean> = dataStore.data.map { it[Keys.HAS_RATED] ?: false }

    override val lastPromptTimeInMillis: Flow<Long?> = dataStore.data.map { it[Keys.LAST_PROMPT_TIME] }

    override val ratePoint: Flow<Int> = dataStore.data.map { it[Keys.RATE_POINT] ?: 0 }

    override suspend fun setHasRated(hasRated: Boolean) {
        dataStore.edit { it[Keys.HAS_RATED] = hasRated }
    }

    override suspend fun setLastPromptTime(timestamp: Long) {
        dataStore.edit { it[Keys.LAST_PROMPT_TIME] = timestamp }
    }

    override suspend fun setRatePoint(point: Int) {
        dataStore.edit { it[Keys.RATE_POINT] = point }
    }
}
