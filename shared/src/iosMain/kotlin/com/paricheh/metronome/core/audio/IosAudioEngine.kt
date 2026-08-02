package com.paricheh.metronome.core.audio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * iOS stub implementation of [AudioEngine].
 *
 * This implementation allows the KMP project to build but does not provide
 * any functionality. It throws [NotImplementedError] when started.
 */
class IosAudioEngine : AudioEngine {
    override fun observeAudioFrames(): Flow<AudioFrame> = emptyFlow()

    override suspend fun start() {
        throw NotImplementedError("AudioEngine is not implemented on iOS yet.")
    }

    override suspend fun stop() {
        // No-op
    }

    override fun isRunning(): Boolean = false
}
