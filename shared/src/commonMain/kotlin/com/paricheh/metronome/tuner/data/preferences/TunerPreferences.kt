package com.paricheh.metronome.tuner.data.preferences

import kotlinx.coroutines.flow.Flow

interface TunerPreferences {
    val selectedInstrumentKey: Flow<String>
    suspend fun setSelectedInstrumentKey(key: String)
}