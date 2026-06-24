package com.paricheh.metronome.metronome.data

import com.paricheh.metronome.core.Note
import com.paricheh.metronome.core.TimeSignature
import kotlinx.coroutines.flow.Flow

interface MetronomeSettings {
    val preferences: Flow<MetronomePreferences>

    suspend fun updateTempo(tempo: Int)
    suspend fun updateTimeSignature(timeSignature: TimeSignature?)
    suspend fun updateBarStructure(barStructure: List<Note>?)
    suspend fun updateVibrationEnabled(enabled: Boolean)
}