package com.paricheh.metronome.metronome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.data.MetronomeSettings
import com.paricheh.metronome.sound.MetronomeSoundPlayer
import com.paricheh.metronome.utils.getTempoMarkingByBpm
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MetronomeViewModel(
    private val settings: MetronomeSettings,
    private val soundPlayer: MetronomeSoundPlayer
) : ViewModel() {
    private val _currentTempoBpm = MutableStateFlow(100f)
    val currentTempoBpm = _currentTempoBpm.asStateFlow()

    private val _currentBeat = MutableStateFlow(1) // Track current beat (1-based)
    val currentBeat = _currentBeat.asStateFlow()

    private val _currentTempoMarking = MutableStateFlow(
        getTempoMarkingByBpm(currentTempoBpm.value.toInt())
    )
    val currentTempoMarkings = _currentTempoMarking.asStateFlow()

    private val _durationInMillisecond = MutableStateFlow(
        60000 / currentTempoBpm.value
    )
    val durationInMillisecond = _durationInMillisecond.asStateFlow()


    private val _isMetronomeStarted = MutableStateFlow(false)
    val isMetronomeStarted = _isMetronomeStarted.asStateFlow()

    private val _pendulumAngle = MutableStateFlow(0f)
    val pendulumAngle = _pendulumAngle.asStateFlow()

    val metronomePreferences = settings.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    init {
        observeTempoMarking()
        observeDurationChanges()
        observeStartingMetronome()
    }

    fun setTempo(bpm: Float) {
        _currentTempoBpm.value = bpm
    }

    fun startMetronome(pendulumAngle: Float) {
        _pendulumAngle.value = pendulumAngle
        _isMetronomeStarted.value = true
    }

    fun stopMetronome() {
        _pendulumAngle.value = 0f
        _isMetronomeStarted.value = false
    }

    fun setPendulumAngle(angle: Float) {
        _pendulumAngle.value = angle
    }

    private fun observeTempoMarking() {
        currentTempoBpm.onEach { newTempo ->
            _currentTempoMarking.value =
                getTempoMarkingByBpm(newTempo.toInt())
        }.launchIn(viewModelScope)
    }

    private fun observeDurationChanges() {
        currentTempoBpm.onEach { newTempo ->
            _durationInMillisecond.value =
                getDurationOnEachBeatByBpm(newTempo)
        }.launchIn(viewModelScope)
    }

    private fun getDurationOnEachBeatByBpm(newTempo: Float): Float =
        60000 / newTempo

    private fun observeStartingMetronome() {
        viewModelScope.launch {
            isMetronomeStarted.collectLatest { isStarted ->
                _currentBeat.value = 1
                while (isStarted) {
                    val beatsPerMeasure = settings.preferences.first().timeSignatureBeats
                    val accentEnabled = settings.preferences.first().accentFirstBeat

                    val isAccent = accentEnabled && (_currentBeat.value == 1)

                    _pendulumAngle.value = if (pendulumAngle.value > 0) {
                        -25f
                    } else {
                        25f
                    }
                    _currentBeat.value = if (_currentBeat.value >= beatsPerMeasure) {
                        1
                    } else {
                        _currentBeat.value + 1
                    }
                    delay(durationInMillisecond.value.toLong())
                    soundPlayer.playTick(isAccent)
                }
            }
        }
    }
}