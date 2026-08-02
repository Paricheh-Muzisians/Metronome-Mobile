package com.paricheh.metronome.tuner.data.repository

import com.paricheh.metronome.core.audio.AudioEngine
import com.paricheh.metronome.tuner.data.detector.PitchDetector
import com.paricheh.metronome.tuner.data.normalizer.FrequencyNormalizer
import com.paricheh.metronome.tuner.data.tuner.Tuner
import com.paricheh.metronome.tuner.data.tuner.TunerState
import kotlinx.coroutines.flow.*

/**
 * Implementation of [TunerRepository] that orchestrates the audio pipeline.
 */
class TunerRepositoryImpl(
    private val audioEngine: AudioEngine,
    private val pitchDetector: PitchDetector,
    private val normalizer: FrequencyNormalizer,
    private val tuner: Tuner
) : TunerRepository {

    override fun observeTuner(): Flow<TunerState> {
        return audioEngine.observeAudioFrames()
            .map { frame ->
                val pitchResult = pitchDetector.detect(frame)
                val normalizedFreq = normalizer.normalize(pitchResult.frequency, pitchResult.confidence)
                val tunerResult = tuner.process(normalizedFreq, pitchResult.confidence)
                
                if (tunerResult != null) {
                    TunerState.Detected(tunerResult)
                } else if (pitchResult.confidence > 0.3f) {
                    TunerState.Listening
                } else {
                    TunerState.NoSignal
                }
            }
            .onStart { emit(TunerState.Idle) }
            .distinctUntilChanged()
    }

    override suspend fun start() {
        normalizer.reset()
        audioEngine.start()
    }

    override suspend fun stop() {
        audioEngine.stop()
    }

    override fun isActive(): Boolean = audioEngine.isRunning()
}
