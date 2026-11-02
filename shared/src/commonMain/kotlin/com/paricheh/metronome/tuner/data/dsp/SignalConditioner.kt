package com.paricheh.metronome.tuner.data.dsp

import kotlin.math.log10
import kotlin.math.sqrt

/**
 * High-performance DSP signal conditioning for acoustic musical instrument tuning.
 *
 * Implements:
 * 1. 1st-order DC-blocking filter (R = 0.995) to eliminate ADC voltage offset.
 * 2. Dynamic RMS energy gate in dBFS with ambient noise tracking.
 */
class SignalConditioner(
    private var silenceFloorDb: Float = -50.0f
) {
    // DC Blocking state
    private var dcXPrev = 0f
    private var dcYPrev = 0f
    private val dcPole = 0.995f

    // Ambient noise floor tracking
    private var ambientNoiseLevelDb = -60.0f
    private val noiseFloorAlpha = 0.05f

    fun setSilenceFloorDb(floorDb: Float) {
        silenceFloorDb = floorDb
    }

    fun resetFilterStates() {
        dcXPrev = 0f
        dcYPrev = 0f
        ambientNoiseLevelDb = -60.0f
    }

    /**
     * Applies DC blocking in-place to [samples].
     * Returns true if signal power exceeds the noise gate, false if it should be silenced.
     */
    fun process(samples: FloatArray): Boolean {
        if (samples.isEmpty()) return false

        var energySum = 0f
        for (i in samples.indices) {
            val input = samples[i]

            // 1st-order recursive DC blocking filter: y[n] = x[n] - x[n-1] + R * y[n-1]
            val dcOut = input - dcXPrev + dcPole * dcYPrev
            dcXPrev = input
            dcYPrev = dcOut

            samples[i] = dcOut
            energySum += dcOut * dcOut
        }

        val rms = sqrt(energySum / samples.size)
        val rmsDb = if (rms > 1e-5f) 20f * log10(rms) else -100f

        // Ambient noise tracking
        if (rmsDb < ambientNoiseLevelDb + 6f) {
            ambientNoiseLevelDb = (1f - noiseFloorAlpha) * ambientNoiseLevelDb + noiseFloorAlpha * rmsDb
        }

        val gateThreshold = maxOf(silenceFloorDb, ambientNoiseLevelDb + 3.0f)
        return rmsDb >= gateThreshold
    }
}
