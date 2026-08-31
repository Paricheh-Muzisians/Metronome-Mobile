package com.paricheh.metronome.tuner.ui.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.tuner.data.repository.TunerRepository
import com.paricheh.metronome.tuner.data.theory.Instrument
import com.paricheh.metronome.tuner.data.theory.NoteInfo
import com.paricheh.metronome.tuner.data.tuner.TunerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TunerViewModel(
    private val tunerRepository: TunerRepository,
    val currentInstrument: Instrument,
) : ViewModel() {
    var observeTunerJob: Job? = null


    private val _selectedNote = MutableStateFlow<NoteInfo?>(null)
    val selectedNote = _selectedNote.asStateFlow()

    private val _tunerState = MutableStateFlow<TunerState>(TunerState.Idle)
    val tunerState = _tunerState.asStateFlow()


    init {
        startObservingTunerResult(note = null)
        viewModelScope.launch {
            tunerRepository.start()
        }

        _selectedNote.onEach {
            startObservingTunerResult(note = it)
        }.launchIn(viewModelScope)
    }

    fun startObservingTunerResult(note: NoteInfo?) {
        viewModelScope.launch {
            observeTunerJob?.cancelAndJoin()
            observeTunerJob = tunerRepository.observeTuner(note)
                .onEach {
                    _tunerState.emit(it)
                }
                .launchIn(viewModelScope)
        }
    }

    fun selectNote(note: NoteInfo?) {
        viewModelScope.launch {
            _selectedNote.emit(note)
        }
    }
}