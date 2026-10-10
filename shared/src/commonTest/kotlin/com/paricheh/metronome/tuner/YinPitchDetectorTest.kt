package com.paricheh.metronome.tuner

import com.paricheh.metronome.tuner.data.detector.YinPitchDetector
import com.paricheh.metronome.tuner.test.SyntheticAudioGenerator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class YinPitchDetectorTest {

    @Test
    fun testSilenceReturnsZeroPitch() {
        val detector = YinPitchDetector()
        val noiseFrame = SyntheticAudioGenerator.generateNoiseFrame(rmsAmplitude = 0.001f)

        val result = detector.detect(noiseFrame)

        assertEquals(0f, result.frequency, "Silence/ambient mic noise must return 0 Hz frequency")
        assertEquals(0f, result.confidence, "Silence/ambient mic noise must return 0 confidence")
    }

    @Test
    fun testPureSineA4Accuracy() {
        val detector = YinPitchDetector()
        val sineFrame = SyntheticAudioGenerator.generateSineFrame(frequency = 440f, amplitude = 0.5f)

        val result = detector.detect(sineFrame)

        assertTrue(result.frequency > 0f, "Should detect frequency for 440 Hz sine")
        val freqDiff = kotlin.math.abs(result.frequency - 440f)
        assertTrue(freqDiff <= 2.0f, "Detected frequency ${result.frequency} Hz should be within 2 Hz of 440 Hz")
        assertTrue(result.confidence > 0.8f, "Confidence should be high for clean sine wave")
    }

    @Test
    fun testHarmonicShiftDoesNotJumpOctave() {
        val detector = YinPitchDetector()
        val harmonicFrame = SyntheticAudioGenerator.generateHarmonicFrame(
            fundamental = 220f,
            harmonicAmplitudes = listOf(0.3f, 0.7f)
        )

        val result = detector.detect(harmonicFrame)

        assertTrue(result.frequency > 0f, "Should detect frequency for harmonic note")
        val freqDiffF0 = kotlin.math.abs(result.frequency - 220f)
        assertTrue(
            freqDiffF0 <= 5.0f,
            "Detector should lock onto fundamental 220 Hz instead of 440 Hz octave jump. Got: ${result.frequency} Hz"
        )
    }
}
