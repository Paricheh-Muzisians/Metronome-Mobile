package com.paricheh.metronome.tuner

import com.paricheh.metronome.tuner.data.detector.InstrumentPitchTracker
import com.paricheh.metronome.tuner.data.detector.YinPitchDetector
import com.paricheh.metronome.tuner.data.tuner.ChromaticTuner
import com.paricheh.metronome.tuner.model.MusicalNote
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.test.SyntheticAudioGenerator
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String
import com.paricheh.metronome.tuner.ui.utils.instrument.SetarMahoor
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
        val freqDiff = abs(result.frequency - 440f)
        assertTrue(freqDiff <= 2.0f, "Detected frequency ${result.frequency} Hz should be within 2 Hz of 440 Hz")
        assertTrue(result.confidence > 0.8f, "Confidence should be high for clean sine wave")
    }

    @Test
    fun testGuitarLowE2Detection() {
        val detector = YinPitchDetector()
        // Low E2 is ~82.41 Hz
        val frame = SyntheticAudioGenerator.generateSineFrame(frequency = 82.41f, amplitude = 0.6f, durationMs = 80)

        val result = detector.detect(frame)

        assertTrue(result.frequency > 0f, "Should detect frequency for Guitar Low E2 (82.4 Hz)")
        val diff = abs(result.frequency - 82.41f)
        assertTrue(diff <= 2.0f, "Detected frequency ${result.frequency} Hz should be within 2 Hz of 82.41 Hz")
    }

    @Test
    fun testSetarC3Detection() {
        val detector = YinPitchDetector()
        // Setar 4th string Do3 is ~130.81 Hz
        val frame = SyntheticAudioGenerator.generateSineFrame(frequency = 130.81f, amplitude = 0.5f, durationMs = 60)

        val result = detector.detect(frame)

        assertTrue(result.frequency > 0f, "Should detect frequency for Setar Do3 (130.81 Hz)")
        val diff = abs(result.frequency - 130.81f)
        assertTrue(diff <= 2.0f, "Detected frequency ${result.frequency} Hz should be within 2 Hz of 130.81 Hz")
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
        val freqDiffF0 = abs(result.frequency - 220f)
        assertTrue(
            freqDiffF0 <= 5.0f,
            "Detector should lock onto fundamental 220 Hz instead of 440 Hz octave jump. Got: ${result.frequency} Hz"
        )
    }

    @Test
    fun testInstrumentPitchTrackerTracksDecayWithoutOctaveJump() {
        val tracker = InstrumentPitchTracker()
        tracker.configureForInstrument(Guitar6String())

        // 1 second decaying note at 110 Hz (Guitar A2 string)
        val decayingFrames = SyntheticAudioGenerator.generateDecayingNote(
            fundamental = 110f,
            decayRateF0 = 1.5f,
            decayRate2F0 = 0.5f,
            durationMs = 800,
            frameSizeMs = 40
        )

        var detectedCount = 0
        for (frame in decayingFrames) {
            val result = tracker.detect(frame)
            if (result.frequency > 0f) {
                detectedCount++
                // Must remain near 110 Hz throughout the decay tail without jumping to 220 Hz
                val diff = abs(result.frequency - 110f)
                assertTrue(
                    diff <= 6.0f,
                    "Decay tracking jumped away from 110 Hz fundamental! Got: ${result.frequency} Hz"
                )
            }
        }
        assertTrue(detectedCount > 5, "Should have tracked multiple frames across decay tail")
    }

    @Test
    fun testChromaticTunerAutoAndTargetModes() {
        val tuner = ChromaticTuner()
        val guitar = Guitar6String()

        // 1. Auto mode with exact A4 (440 Hz)
        val resultA4 = tuner.process(
            frequency = 440f,
            confidence = 0.9f,
            targetNote = null,
            notes = guitar.notes
        )
        assertNotNull(resultA4)
        assertEquals(MusicalNote.La, resultA4.noteInfo.note)
        assertEquals(4, resultA4.noteInfo.octave)
        assertTrue(abs(resultA4.centsDifference) < 1.0f)

        // 2. Auto mode with Guitar E2 string slightly sharp (83.0 Hz vs 82.41 Hz)
        val resultE2 = tuner.process(
            frequency = 83.0f,
            confidence = 0.9f,
            targetNote = null,
            notes = guitar.notes
        )
        assertNotNull(resultE2)
        assertEquals(MusicalNote.Mi, resultE2.noteInfo.note)
        assertEquals(2, resultE2.noteInfo.octave)
        assertTrue(resultE2.centsDifference > 0f, "Should indicate string is sharp")

        // 3. Target note mode with intentionally flat string (e.g., tuning up to E2 from 78.0 Hz)
        val targetE2 = guitar.notes.first { it.note == MusicalNote.Mi && it.octave == 2 }
        val resultTargetFlat = tuner.process(
            frequency = 78.0f,
            confidence = 0.85f,
            targetNote = targetE2,
            notes = guitar.notes
        )
        assertNotNull(resultTargetFlat)
        assertEquals(MusicalNote.Mi, resultTargetFlat.noteInfo.note)
        assertTrue(resultTargetFlat.centsDifference < -50f, "Should report string is flat")
    }
}
