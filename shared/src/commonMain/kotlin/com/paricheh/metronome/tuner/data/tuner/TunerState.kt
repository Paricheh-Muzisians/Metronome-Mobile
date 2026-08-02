package com.paricheh.metronome.tuner.data.tuner

/**
 * Represents the high-level state of the tuner.
 */
sealed class TunerState {
    data object Idle : TunerState()
    data object Listening : TunerState()
    data object NoSignal : TunerState()
    data class Detected(val result: TunerResult) : TunerState()
}
