package com.paricheh.metronome.metronome.ui.metronome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.core.TimeSignatureType
import com.paricheh.metronome.core.getTempoMarkingByBpm
import com.paricheh.metronome.core.soundplayer.MetronomeSoundPlayer
import com.paricheh.metronome.core.vibrator.MetronomeVibrator
import com.paricheh.metronome.metronome.data.MetronomeSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MetronomeViewModel(
    private val settings: MetronomeSettings,
    private val soundPlayer: MetronomeSoundPlayer,
    private val vibrator: MetronomeVibrator,
) : ViewModel() {
    var setTempoJob: Job? = null
    val metronomePreferences = settings.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val _currentTempoBpm = MutableStateFlow(
        metronomePreferences.value
            ?.tempo
            ?.toFloat()
            ?: 100f
    )
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
        observeSettingTempo()
        observeTempoMarking()
        observeDurationChanges()
        observeStartingMetronome()
        observeSoundPlaying()
    }

    private fun observeSettingTempo() {
        metronomePreferences
            .map { it?.tempo }
            .distinctUntilChanged()
            .onEach {
                if (it != null) {
                    setTempo(it.toFloat())
                }
            }
            .launchIn(viewModelScope)
    }

    fun setTempo(bpm: Float) {

        if (bpm.roundToInt() % 5 == 0 && _currentTempoBpm.value.roundToInt() != bpm.roundToInt()) {
            vibrator.vibrateOnScroll()
        }

        _currentTempoBpm.value = bpm

        setTempoJob?.cancel()
        setTempoJob = viewModelScope.launch {
            settings.updateTempo(bpm.roundToInt())
        }
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

    fun markUnboardingAsSeen() {
        viewModelScope.launch {
            settings.updateHasSeenUnboarding(true)
        }
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

    private fun observeSoundPlaying() {
        viewModelScope.launch {
            isMetronomeStarted.collectLatest { isStarted ->
                if (!isStarted) return@collectLatest

                val selectedTimeSignature = metronomePreferences.value
                    ?.selectedTimeSignature

                val actualBarStructure = selectedTimeSignature
                    ?.defaultBarsStructure

                val convertedBarStructure = metronomePreferences.value
                    ?.selectedBarStructure

                when {
                    // Normal Playing
                    selectedTimeSignature == null -> {
                        while (true) {
                            delay(durationInMillisecond.value.toLong())
                            soundPlayer.playTick()
                            if (metronomePreferences.value?.vibrationEnabled == true) {
                                vibrator.vibrateOnTick()
                            }
                        }
                    }

                    // only timeSignature was selected and not converted
                    convertedBarStructure == null -> {
                        while (true) {
                            var i = 0

                            do {
                                delay(durationInMillisecond.value.toLong())

                                if (metronomePreferences.value?.vibrationEnabled == true) {
                                    vibrator.vibrateOnTick()
                                }

                                if (selectedTimeSignature.defaultBarsStructure[i++].isAccent) {
                                    soundPlayer.playAccent()
                                } else {
                                    soundPlayer.playTick()

                                }
                            } while (i <= selectedTimeSignature.defaultBarsStructure.size - 1)
                        }
                    }

                    selectedTimeSignature.type == TimeSignatureType.Irregular -> {
                        val convertedDivision =
                            convertedBarStructure.size / actualBarStructure!!.size

                        val durationForConverted =
                            durationInMillisecond.value / convertedDivision

                        while (true) {
                            delay(durationForConverted.toLong())
                            soundPlayer.playSubBeat()
                        }
                    }

                    else -> {
                        val convertedDivision =
                            convertedBarStructure.size / actualBarStructure!!.size

                        val durationForConverted =
                            durationInMillisecond.value / convertedDivision

                        while (true) {
                            var i = 0
                            do {
                                repeat(convertedDivision - 1) {
                                    delay(durationForConverted.toLong())
                                    soundPlayer.playSubBeat()
                                }

                                delay(durationForConverted.toLong())

                                val isAccent = actualBarStructure
                                    .getOrNull(i++)
                                    ?.isAccent
                                    ?: false

                                if (metronomePreferences.value?.vibrationEnabled == true) {
                                    vibrator.vibrateOnTick()
                                }

                                if (isAccent) {
                                    soundPlayer.playAccent()
                                } else {
                                    soundPlayer.playTick()
                                }

                            } while (i <= actualBarStructure.lastIndex)
                        }
                    }
                }
            }
        }
    }

    private fun observeStartingMetronome() {
        viewModelScope.launch {
            isMetronomeStarted.collectLatest { isStarted ->
                if (!isStarted) return@collectLatest

                while (true) {
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
