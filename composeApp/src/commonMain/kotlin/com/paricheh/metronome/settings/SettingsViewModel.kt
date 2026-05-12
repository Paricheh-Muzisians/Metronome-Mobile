package com.paricheh.metronome.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.data.MetronomePreferences
import com.paricheh.metronome.data.MetronomeSettings
import com.paricheh.metronome.utils.TimeSignature
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

    fun updateTimeSignature(timeSignature: TimeSignature?) {
        viewModelScope.launch {
            settings.updateTimeSignature(timeSignature)
        }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settings.updateVibrationEnabled(enabled)
        }
    }
}
