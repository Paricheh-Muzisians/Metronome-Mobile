package com.paricheh.metronome.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.data.MetronomePreferences
import com.paricheh.metronome.data.MetronomeSettings
import com.paricheh.metronome.utils.Note
import com.paricheh.metronome.utils.TimeSignature
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settings: MetronomeSettings,
) : ViewModel() {
    val preferences: StateFlow<MetronomePreferences?> = settings.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        preferences
            .map { it?.selectedTimeSignature to it?.selectedBarStructure }
            .distinctUntilChanged()
            .onEach { (timeSignature, barStruct) ->
                if (timeSignature != null || barStruct != null)
                    convertBarStructure(barStruct?.firstOrNull())
            }.launchIn(viewModelScope)
    }

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

    fun convertBarStructure(unit: Note?) {
        viewModelScope.launch {

            val defaultBarStructure = preferences.value
                ?.selectedTimeSignature
                ?.defaultBarsStructure

            if (unit == null || defaultBarStructure == null) {
                return@launch settings.updateBarStructure(null)
            }

            val convertedBarStructure = defaultBarStructure.flatMap { note ->
                buildList {
                    repeat(unit.weight / note.weight) {
                        add(unit)
                    }
                    if (note.isDot) {
                        repeat(unit.weight / note.weight / 2) {
                            add(unit)
                        }
                    }
                }
            }

            settings.updateBarStructure(convertedBarStructure)
        }
    }
}
