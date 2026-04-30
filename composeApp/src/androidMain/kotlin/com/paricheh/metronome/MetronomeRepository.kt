package com.paricheh.metronome

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.paricheh.metronome.settings.MetronomePreferences
import com.paricheh.metronome.settings.MetronomeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "metronome_preferences")

private lateinit var appContext: Context

fun initializeMetronomeSettings(context: Context) {
    appContext = context.applicationContext
}

actual fun createMetronomeSettings(): MetronomeSettings = AndroidMetronomeSettings(appContext.dataStore)

private class AndroidMetronomeSettings(
    private val dataStore: DataStore<Preferences>
) : MetronomeSettings {

    private object Keys {
        val TEMPO = intPreferencesKey("tempo")
        val TIME_SIGNATURE_BEATS = intPreferencesKey("time_signature_beats")
        val TIME_SIGNATURE_BEAT_UNIT = intPreferencesKey("time_signature_beat_unit")
        val ACCENT_FIRST_BEAT = booleanPreferencesKey("accent_first_beat")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }

    override val preferences: Flow<MetronomePreferences> = dataStore.data.map { prefs ->
        MetronomePreferences(
            tempo = prefs[Keys.TEMPO] ?: 120,
            timeSignatureBeats = prefs[Keys.TIME_SIGNATURE_BEATS] ?: 4,
            timeSignatureBeatUnit = prefs[Keys.TIME_SIGNATURE_BEAT_UNIT] ?: 4,
            accentFirstBeat = prefs[Keys.ACCENT_FIRST_BEAT] ?: true,
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: false
        )
    }

    override suspend fun updateTempo(tempo: Int) {
        dataStore.edit { it[Keys.TEMPO] = tempo }
    }

    override suspend fun updateTimeSignature(beats: Int, beatUnit: Int) {
        dataStore.edit {
            it[Keys.TIME_SIGNATURE_BEATS] = beats
            it[Keys.TIME_SIGNATURE_BEAT_UNIT] = beatUnit
        }
    }

    override suspend fun updateAccentFirstBeat(enabled: Boolean) {
        dataStore.edit { it[Keys.ACCENT_FIRST_BEAT] = enabled }
    }

    override suspend fun updateSoundEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    override suspend fun updateVibrationEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }
}
