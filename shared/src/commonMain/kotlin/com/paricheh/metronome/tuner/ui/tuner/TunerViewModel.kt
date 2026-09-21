package com.paricheh.metronome.tuner.ui.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.tuner.data.preferences.TunerPreferences
import com.paricheh.metronome.tuner.data.repository.TunerRepository
import com.paricheh.metronome.tuner.model.TunerState
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import com.paricheh.metronome.tuner.ui.utils.instrument.Piano88
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TunerViewModel(
    private val tunerRepository: TunerRepository,
    private val preferences: TunerPreferences,
) : ViewModel() {
    var observeTunerJob: Job? = null

    private val _selectedNote = MutableStateFlow<NoteInfo?>(null)
    val selectedNote = _selectedNote.asStateFlow()

    private val _tunerState = MutableStateFlow<TunerState>(TunerState.Idle)
    val tunerState = _tunerState.asStateFlow()

    private val _selectedInstrument = MutableStateFlow<Instrument?>(null)
    val selectedInstrument = _selectedInstrument.asStateFlow()

    init {
        observeInstrument()
        handleInstrumentChange()
        startTuner()
    }

    fun startTuner() {
        viewModelScope.launch {
            tunerRepository.start()
        }
    }

    fun selectNote(note: NoteInfo?) {
        viewModelScope.launch {
            _selectedNote.emit(note)
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
        preferences.selectedInstrumentKey
            .onEach { key ->
                val instrument = when (key) {
                    Guitar6String.KEY -> Guitar6String()
                    Piano88.KEY -> Piano88()
                    else -> error("unknown instrument :$key :(")
                }
                _selectedInstrument.emit(instrument)
            }
            .catch {
                //TODO Log
            }
            .launchIn(viewModelScope)
    }
}