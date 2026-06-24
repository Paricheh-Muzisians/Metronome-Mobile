package com.paricheh.metronome.metronome.data

import com.paricheh.metronome.core.Note
import com.paricheh.metronome.core.TimeSignature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults


internal class IosMetronomeSettings : MetronomeSettings {

    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val _preferences = MutableStateFlow(loadPreferences())

    override val preferences: Flow<MetronomePreferences> = _preferences.asStateFlow()

    private fun loadPreferences(): MetronomePreferences {
        return MetronomePreferences(
            tempo = userDefaults.integerForKey("tempo").toInt().takeIf { it > 0 } ?: 120,
            vibrationEnabled = userDefaults.boolForKey("vibration_enabled"),
            selectedTimeSignature = (userDefaults.objectForKey("time_signature") as? String?)?.let {
                Json.decodeFromString<TimeSignature>(it)
            },
            selectedBarStructure = (userDefaults.objectForKey("bar_structure") as? String?)?.let {
                Json.decodeFromString<List<Note>>(it)
            }
        )
    }

    private fun saveAndUpdate() {
        _preferences.value = loadPreferences()
    }

    override suspend fun updateTempo(tempo: Int) {
        userDefaults.setInteger(tempo.toLong(), "tempo")
        saveAndUpdate()
    }

    override suspend fun updateTimeSignature(timeSignature: TimeSignature?) {
        userDefaults.setObject(
            value = timeSignature?.let { Json.encodeToString(it) },
            forKey = "time_signature"
        )
        saveAndUpdate()
    }

    override suspend fun updateBarStructure(barStructure: List<Note>?) {
        userDefaults.setObject(
            value = Json.encodeToString(barStructure),
            forKey = "bar_structure"
        )
        saveAndUpdate()
    }

    override suspend fun updateVibrationEnabled(enabled: Boolean) {
        userDefaults.setBool(enabled, "vibration_enabled")
        saveAndUpdate()
    }
}
