package com.paricheh.metronome.tuner.data.repository

import com.paricheh.metronome.tuner.data.theory.NoteInfo
import com.paricheh.metronome.tuner.data.tuner.TunerState
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
    fun observeTuner(targetNote: NoteInfo?): Flow<TunerState>

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
