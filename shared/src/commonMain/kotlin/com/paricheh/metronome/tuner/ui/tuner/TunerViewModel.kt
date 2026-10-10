package com.paricheh.metronome.tuner.ui.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.core.analytics.AnalyticsManager
import com.paricheh.metronome.core.soundplayer.TunerSoundPlayer
import com.paricheh.metronome.rating.data.repository.RatingRepository
import com.paricheh.metronome.tuner.data.preferences.TunerPreferences
import com.paricheh.metronome.tuner.data.repository.TunerRepository
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.model.TunerState
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import com.paricheh.metronome.tuner.ui.utils.instrument.Setar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TunerViewModel(
    private val tunerRepository: TunerRepository,
    private val preferences: TunerPreferences,
    private val tunerSoundPlayer: TunerSoundPlayer,
    private val permissionChecker: PermissionChecker,
    private val ratingRepository: RatingRepository,
    private val analyticsManager: AnalyticsManager,
) : ViewModel() {
    private var observeTunerJob: Job? = null

    private val _selectedNote = MutableStateFlow<NoteInfo?>(null)
    val selectedNote = _selectedNote.asStateFlow()

    private val _tunerState = MutableStateFlow<TunerState>(TunerState.Idle)
    val tunerState = _tunerState.asStateFlow()

    private val _selectedInstrument = MutableStateFlow<Instrument?>(null)
    val selectedInstrument = _selectedInstrument.asStateFlow()

    private val _allInstruments = MutableStateFlow<List<Instrument>>(listOf())
    val allInstruments = _allInstruments.asStateFlow()

    private val _showPermissionDialog = MutableStateFlow(false)
    val showPermissionDialog = _showPermissionDialog.asStateFlow()

    private val _shouldRequestPermission = Channel<Boolean>(Channel.BUFFERED)
    val shouldRequestPermission = _shouldRequestPermission.receiveAsFlow()

    init {
        observeInstrument()
        getAllInstruments()
        handleInstrumentChange()
    }

    fun increaseRatingPoint() {
        viewModelScope.launch(Dispatchers.IO) {
            ratingRepository.increaseRatingPoint(5)
        }
    }

    fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            _showPermissionDialog.value = false
            startTuner()
        } else {
            analyticsManager.track(
                "tuner_ungranted_microphone_permission"
            )
            _showPermissionDialog.value = true
        }
    }

    fun checkPermission() {
        if (permissionChecker.isAudioPermissionGranted()) {
            _showPermissionDialog.value = false
            startTuner()
        } else {
            stopTuner()
            if (!_showPermissionDialog.value) {
                _shouldRequestPermission.trySend(true)
            }
        }
    }

    fun startTuner() {
        viewModelScope.launch {
            tunerRepository.startTuner()
        }
    }

    fun stopTuner() {
        viewModelScope.launch {
            tunerRepository.stopTuner()
        }
    }

    override fun onCleared() {
        stopTuner()
        super.onCleared()
    }

    fun selectNote(note: NoteInfo?) {
        viewModelScope.launch {
            if (selectedInstrument.value is Guitar6String && note != null) {
                tunerSoundPlayer.playGuitarSample(note)
            }
            _selectedNote.emit(note)
        }
    }

    fun selectInstrument(instrument: Instrument) {
        viewModelScope.launch {
            analyticsManager.track(
                "select_instrument",
                parameters = mapOf(
                    "instrument" to instrument.key
                )
            )
            preferences.setSelectedInstrumentKey(instrument.key)
        }
    }

    private fun getAllInstruments() {
        viewModelScope.launch {
            _allInstruments.emit(
                tunerRepository.getAllInstruments()
            )
        }
    }

    private fun handleInstrumentChange() {
        combine(
            selectedInstrument,
            selectedNote
        ) { instrument, note ->
            if (instrument != null) {
                startObservingTunerResult(
                    note = note,
                    notes = instrument.notes
                )
            }
        }.launchIn(viewModelScope)
    }


    private fun startObservingTunerResult(
        note: NoteInfo?,
        notes: List<NoteInfo>,
    ) {
        viewModelScope.launch {
            observeTunerJob?.cancelAndJoin()
            observeTunerJob = tunerRepository.observeTuner(
                targetNote = note,
                notes = notes
            )
                .onEach {
                    _tunerState.emit(it)
                }
                .launchIn(viewModelScope)
        }
    }

    private fun observeInstrument() {
        combine(
            preferences.selectedInstrumentKey,
            allInstruments
        ) { key, instruments ->
            if (instruments.isEmpty()) return@combine null
            instruments.firstOrNull { it.key == key }
                ?: instruments.firstOrNull { key.startsWith("setar") && it is Setar }
                ?: instruments.firstOrNull()
        }.onEach { instrument ->
            instrument?.let {
                _selectedInstrument.emit(it)
            }
        }.catch {
            //TODO Log
        }.launchIn(viewModelScope)
    }
}