package com.paricheh.metronome.tuner.data.detector

import com.paricheh.metronome.core.audio.AudioFrame
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String
import com.paricheh.metronome.tuner.ui.utils.instrument.Instrument
import com.paricheh.metronome.tuner.ui.utils.instrument.Piano88
import com.paricheh.metronome.tuner.ui.utils.instrument.Setar
import kotlin.math.abs
import kotlin.math.log2

/**
 * State machine for plucked string instruments (Guitar, Setar) and acoustic piano.
 *
 * Implements the 2-stage adaptive tracking strategy described in research:
 * 1. Attack Phase: High signal energy, initial tension pitch glide rejection.
 * 2. Decay Phase: Restricts frequency search window tightly around the last verified pitch (±120 cents)
 *    and relaxes threshold to track the weakening fundamental deep into the decay tail without
 *    jumping to lingering upper harmonics or background noise.
 */
class InstrumentPitchTracker(
    private val yinDetector: YinPitchDetector = YinPitchDetector()
) : PitchDetector {

    enum class TrackerState {
        IDLE,
        ATTACK,
        DECAY_TRACKING
    }

    private var currentState = TrackerState.IDLE
    private var lastLockedFrequency = 0f
    private var attackFrameCount = 0
    private var activeInstrument: Instrument? = null

    init {
        configureForInstrument(null)
    }

    fun configureForInstrument(instrument: Instrument?) {
        activeInstrument = instrument
        reset()
        when (instrument) {
            is Guitar6String -> {
                // Guitar standard range: E2 (82.4 Hz) to high register overtones (~1200 Hz)
                yinDetector.configureFrequencyRange(minFreqHz = 70.0f, maxFreqHz = 1200.0f)
                yinDetector.setThreshold(0.12f)
            }
            is Setar -> {
                // Setar range: C3 (130.8 Hz) to ~1500 Hz
                yinDetector.configureFrequencyRange(minFreqHz = 110.0f, maxFreqHz = 1500.0f)
                yinDetector.setThreshold(0.12f)
            }
            is Piano88 -> {
                // Piano 88 keys: A0 (27.5 Hz) to C8 (4186 Hz)
                yinDetector.configureFrequencyRange(minFreqHz = 25.0f, maxFreqHz = 4400.0f)
                yinDetector.setThreshold(0.15f)
            }
            else -> {
                // General chromatic
                yinDetector.configureFrequencyRange(minFreqHz = 25.0f, maxFreqHz = 4400.0f)
                yinDetector.setThreshold(0.15f)
            }
        }
    }

    fun reset() {
        currentState = TrackerState.IDLE
        lastLockedFrequency = 0f
        attackFrameCount = 0
        yinDetector.reset()
    }

    override fun detect(frame: AudioFrame): PitchResult {
        val rawResult = yinDetector.detect(frame)

        // If no signal was detected by the core engine
        if (rawResult.frequency <= 0f || rawResult.confidence < 0.35f) {
            if (currentState == TrackerState.DECAY_TRACKING) {
                // End of note decay tail
                currentState = TrackerState.IDLE
                lastLockedFrequency = 0f
            }
            return PitchResult(0f, 0f)
        }

        val freq = rawResult.frequency

        val isPluckedInstrument = activeInstrument is Guitar6String || activeInstrument is Setar

        if (!isPluckedInstrument) {
            return rawResult
        }

        // 2-Stage Pluck/Decay Tracking for Guitar & Setar
        when (currentState) {
            TrackerState.IDLE -> {
                // Note onset detected: enter attack stage
                currentState = TrackerState.ATTACK
                attackFrameCount = 1
                lastLockedFrequency = freq
                return rawResult
            }
            TrackerState.ATTACK -> {
                attackFrameCount++
                // Transition to decay tracking after ~2 frames (~40-60ms) to allow initial pluck tension spike to settle
                if (attackFrameCount >= 2) {
                    currentState = TrackerState.DECAY_TRACKING
                    lastLockedFrequency = freq
                    // Narrow search window around the verified fundamental for decay tracking
                    val minSearch = (lastLockedFrequency * 0.90f).coerceAtLeast(60f)
                    val maxSearch = (lastLockedFrequency * 1.10f).coerceAtMost(2000f)
                    yinDetector.configureFrequencyRange(minSearch, maxSearch, frame.sampleRate)
                    yinDetector.setThreshold(0.25f) // Relax threshold for decay tail
                }
                return rawResult
            }
            TrackerState.DECAY_TRACKING -> {
                // Check if current frequency is within ±150 cents of locked fundamental
                val centsDiff = abs(1200f * log2(freq / lastLockedFrequency))
                if (centsDiff <= 150f) {
                    lastLockedFrequency = freq
                    return rawResult
                } else {
                    // Jump to a new note detected (new pluck or string change)
                    configureForInstrument(activeInstrument)
                    currentState = TrackerState.ATTACK
                    attackFrameCount = 1
                    lastLockedFrequency = freq
                    return rawResult
                }
            }
        }
    }
}
