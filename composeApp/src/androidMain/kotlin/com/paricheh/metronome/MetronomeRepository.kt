package com.paricheh.metronome

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.paricheh.metronome.data.MetronomePreferences
import com.paricheh.metronome.data.MetronomeSettings
import com.paricheh.metronome.utils.Note
import com.paricheh.metronome.utils.TimeSignature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "metronome_preferences")

private lateinit var appContext: Context

fun initializeMetronomeSettings(context: Context) {
    appContext = context.applicationContext
}

actual fun createMetronomeSettings(): MetronomeSettings =
    AndroidMetronomeSettings(appContext.dataStore)

private class AndroidMetronomeSettings(
    private val dataStore: DataStore<Preferences>,
) : MetronomeSettings {

    private object Keys {
        val TEMPO = intPreferencesKey("tempo")
        val TIME_SIGNATURE = stringPreferencesKey("time_signature")
        val BAR_STRUCTURE = stringPreferencesKey("bar_structure")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }

    override val preferences: Flow<MetronomePreferences> = dataStore.data.map { prefs ->
        MetronomePreferences(
            tempo = prefs[Keys.TEMPO] ?: 120,
            selectedTimeSignature = prefs[Keys.TIME_SIGNATURE]?.let {
                Json.decodeFromString<TimeSignature>(it)
            },
            selectedBarStructure = prefs[Keys.BAR_STRUCTURE]?.let {
                Json.decodeFromString<List<Note>>(it)
            },
            vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: false
        )
    }

    override suspend fun updateTempo(tempo: Int) {
        dataStore.edit { it[Keys.TEMPO] = tempo }
    }

    override suspend fun updateTimeSignature(timeSignature: TimeSignature?) {
        dataStore.edit {
            if (timeSignature != null) {
                it[Keys.TIME_SIGNATURE] = Json.encodeToString(timeSignature)
            } else {
                it.remove(Keys.TIME_SIGNATURE)
            }
        }
    }

    override suspend fun updateBarStructure(barStructure: List<Note>?) {
        dataStore.edit {
            if (barStructure != null) {
                it[Keys.BAR_STRUCTURE] = Json.encodeToString(barStructure)
            } else {
                it.remove(Keys.BAR_STRUCTURE)
            }
        }
    }

    override suspend fun updateVibrationEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }
}
