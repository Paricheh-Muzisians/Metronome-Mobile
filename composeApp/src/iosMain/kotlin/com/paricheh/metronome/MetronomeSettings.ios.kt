package com.paricheh.metronome

import com.paricheh.metronome.settings.MetronomePreferences
import com.paricheh.metronome.settings.MetronomeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

actual fun createMetronomeSettings(): MetronomeSettings = IosMetronomeSettings()

private class IosMetronomeSettings : MetronomeSettings {

    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val _preferences = MutableStateFlow(loadPreferences())

    override val preferences: Flow<MetronomePreferences> = _preferences.asStateFlow()

    private fun loadPreferences(): MetronomePreferences {
        return MetronomePreferences(
            tempo = userDefaults.integerForKey("tempo").toInt().takeIf { it > 0 } ?: 120,
            timeSignatureBeats = userDefaults.integerForKey("time_signature_beats").toInt()
                .takeIf { it > 0 } ?: 4,
            timeSignatureBeatUnit = userDefaults.integerForKey("time_signature_beat_unit").toInt()
                .takeIf { it > 0 } ?: 4,
            accentFirstBeat = userDefaults.objectForKey("accent_first_beat")
                ?.let { userDefaults.boolForKey("accent_first_beat") } ?: true,
            soundEnabled = userDefaults.objectForKey("sound_enabled")
                ?.let { userDefaults.boolForKey("sound_enabled") } ?: true,
            vibrationEnabled = userDefaults.boolForKey("vibration_enabled")
        )
    }

    private fun saveAndUpdate() {
        _preferences.value = loadPreferences()
    }

    override suspend fun updateTempo(tempo: Int) {
        userDefaults.setInteger(tempo.toLong(), "tempo")
        saveAndUpdate()
    }

    override suspend fun updateTimeSignature(beats: Int, beatUnit: Int) {
        userDefaults.setInteger(beats.toLong(), "time_signature_beats")
        userDefaults.setInteger(beatUnit.toLong(), "time_signature_beat_unit")
        saveAndUpdate()
    }

    override suspend fun updateAccentFirstBeat(enabled: Boolean) {
        userDefaults.setBool(enabled, "accent_first_beat")
        saveAndUpdate()
    }

    override suspend fun updateSoundEnabled(enabled: Boolean) {
        userDefaults.setBool(enabled, "sound_enabled")
        saveAndUpdate()
    }

    override suspend fun updateVibrationEnabled(enabled: Boolean) {
        userDefaults.setBool(enabled, "vibration_enabled")
        saveAndUpdate()
    }
}
