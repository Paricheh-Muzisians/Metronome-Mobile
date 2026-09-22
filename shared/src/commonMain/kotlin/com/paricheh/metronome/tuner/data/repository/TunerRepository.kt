package com.paricheh.metronome.tuner.data.repository

import com.paricheh.metronome.tuner.model.TunerState
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Tuner Repository.
 *
 * Orchestrates the pipeline from AudioEngine to TunerState.
 */
interface TunerRepository {
    fun observeTuner(
        targetNote: NoteInfo?,
        notes: List<NoteInfo>,
    ): Flow<TunerState>

    suspend fun startTuner()
    suspend fun stopTuner()
    fun isTuning(): Boolean

    fun getAllInstruments(): List<Instrument>
}
