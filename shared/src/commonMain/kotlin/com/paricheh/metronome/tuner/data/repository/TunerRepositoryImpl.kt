package com.paricheh.metronome.tuner.data.repository

import com.paricheh.metronome.core.audio.AudioEngine
import com.paricheh.metronome.tuner.data.detector.PitchDetector
import com.paricheh.metronome.tuner.data.normalizer.FrequencyNormalizer
import com.paricheh.metronome.tuner.data.tuner.Tuner
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.model.TunerState
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import com.paricheh.metronome.tuner.ui.utils.instrument.Piano88
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarMahoor
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarShurDo
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarShurRe
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarShurSol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Implementation of [TunerRepository] that orchestrates the audio pipeline.
 */
class TunerRepositoryImpl(
    private val audioEngine: AudioEngine,
    private val pitchDetector: PitchDetector,
    private val normalizer: FrequencyNormalizer,
    private val tuner: Tuner,
) : TunerRepository {

    override fun observeTuner(
        targetNote: NoteInfo?,
        notes: List<NoteInfo>,
    ): Flow<TunerState> {
        return audioEngine.observeAudioFrames()
            .map { frame ->
                val pitchResult = pitchDetector.detect(frame)
                val normalizedFreq = normalizer.normalize(
                    frequency = pitchResult.frequency,
                    confidence = pitchResult.confidence
                )

                if (normalizedFreq <= 0f) {
                    TunerState.NoSignal
                } else {
                    val tunerResult = tuner.process(
                        frequency = normalizedFreq,
                        confidence = pitchResult.confidence,
                        targetNote = targetNote,
                        notes = notes,
                    )

                    if (tunerResult != null) {
                        TunerState.Detected(tunerResult)
                    } else {
                        TunerState.Listening
                    }
                }
            }
            .onStart { emit(TunerState.Idle) }
            .distinctUntilChanged()
    }

    override suspend fun startTuner() {
        normalizer.reset()
        audioEngine.start()
    }

    override suspend fun stopTuner() {
        audioEngine.stop()
    }

    override fun isTuning(): Boolean = audioEngine.isRunning()

    override fun getAllInstruments(): List<Instrument> {
        return listOf(
            Guitar6String(),
            SetarMahoor(),
            SetarShurSol(),
            SetarShurDo(),
            SetarShurRe(),
            Piano88()
        )
    }
}
