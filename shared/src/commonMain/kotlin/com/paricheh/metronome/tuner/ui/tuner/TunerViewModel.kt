package com.paricheh.metronome.tuner.ui.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.tuner.data.repository.TunerRepository
import com.paricheh.metronome.tuner.data.theory.Instrument
import com.paricheh.metronome.tuner.data.tuner.TunerState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TunerViewModel(
    private val tunerRepository: TunerRepository,
    val currentInstrument: Instrument,
) : ViewModel() {

    val tunerState = tunerRepository.observeTuner()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TunerState.Idle
        )

    init {
        viewModelScope.launch {
            tunerRepository.start()
        }
    }
}