package com.paricheh.metronome.settings

import kotlinx.coroutines.flow.Flow

interface MetronomeSettings {
    val preferences: Flow<MetronomePreferences>

    suspend fun updateTempo(tempo: Int)
    suspend fun updateTimeSignature(beats: Int, beatUnit: Int)
    suspend fun updateAccentFirstBeat(enabled: Boolean)
    suspend fun updateSoundEnabled(enabled: Boolean)
    suspend fun updateVibrationEnabled(enabled: Boolean)
}
