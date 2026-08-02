package com.paricheh.metronome.core.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Android implementation of [AudioEngine] using [AudioRecord].
 *
 * This implementation captures audio in a background thread, converts PCM data
 * to normalized Floats, and emits [AudioFrame]s through a SharedFlow.
 */
class AndroidAudioEngine : AudioEngine {
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private var audioRecord: AudioRecord? = null
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _audioFrames = MutableSharedFlow<AudioFrame>(extraBufferCapacity = 64)
    private var running = false

    override fun observeAudioFrames(): Flow<AudioFrame> = _audioFrames.asSharedFlow()

    @SuppressLint("MissingPermission")
    override suspend fun start() {
        if (running) return

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            throw IllegalStateException("AudioRecord could not be initialized")
        }

        audioRecord = recorder
        recorder.startRecording()
        running = true

        job = scope.launch {
            val audioData = ShortArray(bufferSize / 2)
            while (isActive && running) {
                val readResult = recorder.read(audioData, 0, audioData.size)
                if (readResult > 0) {
                    val normalizedSamples = FloatArray(readResult)
                    for (i in 0 until readResult) {
                        // Normalize PCM 16-bit to Float (-1.0 to 1.0)
                        normalizedSamples[i] = audioData[i].toFloat() / Short.MAX_VALUE
                    }
                    _audioFrames.emit(
                        AudioFrame(
                            samples = normalizedSamples,
                            sampleRate = sampleRate,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                } else if (readResult < 0) {
                    // Handle error (e.g., AudioRecord.ERROR_INVALID_OPERATION)
                    break
                }
            }
        }
    }

    override suspend fun stop() {
        running = false
        job?.cancelAndJoin()
        job = null

        audioRecord?.apply {
            if (state == AudioRecord.STATE_INITIALIZED) {
                stop()
            }
            release()
        }
        audioRecord = null
    }

    override fun isRunning(): Boolean = running
}
