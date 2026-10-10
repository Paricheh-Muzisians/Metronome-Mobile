package com.paricheh.metronome.core.audio

import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Audio Engine responsible for microphone management.
 *
 * This component is reusable across different audio features and focuses solely on
 * capturing audio from the microphone and providing it as a stream of [AudioFrame]s.
 */
interface AudioEngine {
    /**
     * Provides a flow of [AudioFrame] objects captured from the microphone.
     * The flow will emit frames as long as the engine is running.
     */
    fun observeAudioFrames(): Flow<AudioFrame>

    /**
     * Starts the audio engine, opening the microphone and beginning data capture.
     *
     * @throws IllegalStateException if the microphone is already in use or unavailable.
     * @throws SecurityException if microphone permissions are missing.
     */
    suspend fun start()

    /**
     * Stops the audio engine, closing the microphone and releasing resources.
     */
    suspend fun stop()

    /**
     * Checks if the audio engine is currently running.
     */
    fun isRunning(): Boolean
}
