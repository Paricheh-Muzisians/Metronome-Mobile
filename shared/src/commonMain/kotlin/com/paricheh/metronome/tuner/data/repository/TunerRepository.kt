package com.paricheh.metronome.tuner.data.repository

import com.paricheh.metronome.tuner.model.TunerState
import com.paricheh.metronome.tuner.model.NoteInfo
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Tuner Repository.
 *
 * Orchestrates the pipeline from AudioEngine to TunerState.
 */
interface TunerRepository {
    /**
     * Observes the current state of the tuner.
     */
    fun observeTuner(
        targetNote: NoteInfo?,
        notes: List<NoteInfo>,
    ): Flow<TunerState>

    /**
     * Starts the tuning process.
     */
    suspend fun start()

    /**
     * Stops the tuning process.
     */
    suspend fun stop()

    /**
     * Returns true if the tuner is currently active.
     */
    fun isActive(): Boolean
}
