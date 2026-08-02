package com.paricheh.metronome.tuner.di

import com.paricheh.metronome.tuner.data.detector.PitchDetector
import com.paricheh.metronome.tuner.data.detector.YinPitchDetector
import com.paricheh.metronome.tuner.data.normalizer.FrequencyNormalizer
import com.paricheh.metronome.tuner.data.normalizer.SimpleFrequencyNormalizer
import com.paricheh.metronome.tuner.data.repository.TunerRepository
import com.paricheh.metronome.tuner.data.repository.TunerRepositoryImpl
import com.paricheh.metronome.tuner.data.theory.EqualTemperament
import com.paricheh.metronome.tuner.data.theory.Instrument
import com.paricheh.metronome.tuner.data.theory.Piano88
import com.paricheh.metronome.tuner.data.theory.Temperament
import com.paricheh.metronome.tuner.data.tuner.ChromaticTuner
import com.paricheh.metronome.tuner.data.tuner.Tuner
import org.koin.dsl.module

val tunerModule = module {
    single<Temperament> { EqualTemperament() }
    single<Instrument> { Piano88(get()) }

    single<PitchDetector> { YinPitchDetector() }
    single<FrequencyNormalizer> { SimpleFrequencyNormalizer() }
    single<Tuner> { ChromaticTuner(get()) }

    single<TunerRepository> { TunerRepositoryImpl(get(), get(), get(), get()) }
}
