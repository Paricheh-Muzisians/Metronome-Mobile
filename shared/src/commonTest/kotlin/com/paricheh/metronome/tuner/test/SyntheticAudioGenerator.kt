package com.paricheh.metronome.tuner.test

import com.paricheh.metronome.core.audio.AudioFrame
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Utility for generating synthetic PCM audio frames for deterministic unit tests in KMP.
 */
object SyntheticAudioGenerator {
    const val DEFAULT_SAMPLE_RATE = 44100

    /**
     * Generates an [AudioFrame] containing a pure sine wave.
     */
    fun generateSineFrame(
        frequency: Float,
        durationMs: Int = 40,
        amplitude: Float = 0.5f,
        sampleRate: Int = DEFAULT_SAMPLE_RATE,
        timestamp: Long = 0L
    ): AudioFrame {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = FloatArray(numSamples)
        val angularFreq = 2.0 * PI * frequency / sampleRate

        for (i in 0 until numSamples) {
            samples[i] = (amplitude * sin(angularFreq * i)).toFloat()
        }

        return AudioFrame(samples, sampleRate, timestamp)
    }

    /**
     * Generates an [AudioFrame] with multiple harmonics.
     * @param harmonicAmplitudes List where index 0 = fundamental F0, index 1 = 2F0, index 2 = 3F0, etc.
     */
    fun generateHarmonicFrame(
        fundamental: Float,
        harmonicAmplitudes: List<Float>,
        durationMs: Int = 40,
        sampleRate: Int = DEFAULT_SAMPLE_RATE,
        timestamp: Long = 0L
    ): AudioFrame {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = FloatArray(numSamples)

        for (i in 0 until numSamples) {
            var sum = 0f
            for (h in harmonicAmplitudes.indices) {
                val freq = fundamental * (h + 1)
                val amp = harmonicAmplitudes[h]
                sum += (amp * sin(2.0 * PI * freq * i / sampleRate)).toFloat()
            }
            samples[i] = sum
        }

        return AudioFrame(samples, sampleRate, timestamp)
    }

    /**
     * Generates a sequence of [AudioFrame]s simulating a decaying musical note
     * where the fundamental F0 decays faster than the 2nd harmonic 2F0.
     */
    fun generateDecayingNote(
        fundamental: Float,
        decayRateF0: Float,
        decayRate2F0: Float,
        durationMs: Int = 1000,
        frameSizeMs: Int = 40,
        sampleRate: Int = DEFAULT_SAMPLE_RATE
    ): List<AudioFrame> {
        val frames = mutableListOf<AudioFrame>()
        val totalFrames = durationMs / frameSizeMs
        val samplesPerFrame = (sampleRate * (frameSizeMs / 1000.0)).toInt()

        for (f in 0 until totalFrames) {
            val samples = FloatArray(samplesPerFrame)
            val startTimeSec = (f * frameSizeMs) / 1000.0

            for (i in 0 until samplesPerFrame) {
                val t = startTimeSec + (i.toDouble() / sampleRate)
                val ampF0 = exp(-decayRateF0 * t).toFloat()
                val amp2F0 = exp(-decayRate2F0 * t).toFloat()

                val s1 = ampF0 * sin(2.0 * PI * fundamental * t).toFloat()
                val s2 = amp2F0 * sin(2.0 * PI * (fundamental * 2) * t).toFloat()
                samples[i] = (s1 + s2) * 0.5f
            }
            frames.add(AudioFrame(samples, sampleRate, (f * frameSizeMs).toLong()))
        }

        return frames
    }

    /**
     * Generates an [AudioFrame] containing random white noise with a given RMS amplitude.
     */
    fun generateNoiseFrame(
        rmsAmplitude: Float,
        durationMs: Int = 40,
        sampleRate: Int = DEFAULT_SAMPLE_RATE,
        timestamp: Long = 0L
    ): AudioFrame {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = FloatArray(numSamples)
        val peakAmp = rmsAmplitude * 1.732f

        for (i in 0 until numSamples) {
            samples[i] = ((Random.nextDouble() * 2.0 - 1.0) * peakAmp).toFloat()
        }

        return AudioFrame(samples, sampleRate, timestamp)
    }
}
