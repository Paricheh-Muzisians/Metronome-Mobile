package com.paricheh.metronome.metronome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.data.MetronomeSettings
import com.paricheh.metronome.sound.MetronomeSoundPlayer
import com.paricheh.metronome.utils.TimeSignature
import com.paricheh.metronome.utils.getTempoMarkingByBpm
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MetronomeViewModel(
    settings: MetronomeSettings,
    private val soundPlayer: MetronomeSoundPlayer,
) : ViewModel() {
    private val _currentTempoBpm = MutableStateFlow(100f)
    val currentTempoBpm = _currentTempoBpm.asStateFlow()

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
                val numerator = metronomePreferences.value?.timeSignatureBeats
                val denominator = metronomePreferences.value?.timeSignatureBeatUnit

                val currentTimeSignature = if (numerator != null && denominator != null) {
                    TimeSignature(
                        numerator = numerator,
                        denominator = denominator
                    )
                } else {
                    null
                }

                while (isStarted) {
                    var i = 0
                    do {
                        currentTimeSignature?.defaultBarsStructure
                        _pendulumAngle.value = if (pendulumAngle.value > 0) {
                            -25f
                        } else {
                            25f
                        }
                        delay(durationInMillisecond.value.toLong())

                        val isAccent = currentTimeSignature?.defaultBarsStructure
                            ?.getOrNull(i++)
                            ?.isAccent
                            ?: false

                        soundPlayer.playTick(isAccent)
                    } while (i <= (currentTimeSignature?.defaultBarsStructure?.lastIndex ?: -1))
                }
            }
        }
    }
}