package com.paricheh.metronome.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.data.MetronomePreferences
import com.paricheh.metronome.data.MetronomeSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settings: MetronomeSettings
) : ViewModel() {

    val preferences: StateFlow<MetronomePreferences?> = settings.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updateTempo(tempo: Int) {
        viewModelScope.launch {
            settings.updateTempo(tempo)
        }
    }

    fun updateTimeSignature(beats: Int, beatUnit: Int) {
        viewModelScope.launch {
            settings.updateTimeSignature(beats, beatUnit)
        }
    }

    fun updateAccentFirstBeat(enabled: Boolean) {
        viewModelScope.launch {
            settings.updateAccentFirstBeat(enabled)
        }
    }

    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settings.updateSoundEnabled(enabled)
        }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settings.updateVibrationEnabled(enabled)
        }
    }
}
