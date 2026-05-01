package com.paricheh.metronome.metronome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.data.MetronomeSettings
import com.paricheh.metronome.sound.MetronomeSoundPlayer
import com.paricheh.metronome.utils.getTempoMarkingByBpm
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MetronomeViewModel(
    private val settings: MetronomeSettings,
    private val soundPlayer: MetronomeSoundPlayer
) : ViewModel() {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentBeat = MutableStateFlow(0)
    val currentBeat: StateFlow<Int> = _currentBeat.asStateFlow()

    private var metronomeJob: Job? = null

    fun togglePlayPause() {
        if (_isPlaying.value) {
            stop()
        } else {
            start()
        }
    }

    private fun start() {
        _isPlaying.value = true
        _currentBeat.value = 0

        metronomeJob = viewModelScope.launch {
            // Get current settings
            val prefs = settings.preferences.first()
            val tempo = prefs.tempo
            val beatsPerMeasure = prefs.timeSignatureBeats
            val accentFirstBeat = prefs.accentFirstBeat
            val soundEnabled = prefs.soundEnabled

            // Calculate delay between beats in milliseconds
            val beatInterval = 60_000L / tempo

            while (_isPlaying.value) {
                val beat = _currentBeat.value

                // Play sound if enabled
                if (soundEnabled) {
                    val isAccent = accentFirstBeat && beat == 0
                    soundPlayer.playTick(isAccent)
                }

                // Wait for next beat
                delay(beatInterval)

                // Move to next beat
                _currentBeat.value = (beat + 1) % beatsPerMeasure
            }
        }
    }

    private fun stop() {
        _isPlaying.value = false
        _currentBeat.value = 0
        metronomeJob?.cancel()
        metronomeJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stop()
        soundPlayer.release()
    }

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
                while (isStarted) {
                    _pendulumAngle.value = if (pendulumAngle.value > 0) {
                        -25f
                    } else {
                        25f
                    }
                    delay(durationInMillisecond.value.toLong())
                }
            }
        }
    }
}