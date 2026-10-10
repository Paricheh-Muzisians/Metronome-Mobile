package com.paricheh.metronome.tuner.di

import com.paricheh.metronome.tuner.data.detector.PitchDetector
import com.paricheh.metronome.tuner.data.detector.YinPitchDetector
import com.paricheh.metronome.tuner.data.normalizer.FrequencyNormalizer
import com.paricheh.metronome.tuner.data.normalizer.SimpleFrequencyNormalizer
import com.paricheh.metronome.tuner.data.repository.TunerRepository
import com.paricheh.metronome.tuner.data.repository.TunerRepositoryImpl
import com.paricheh.metronome.tuner.data.tuner.ChromaticTuner
import com.paricheh.metronome.tuner.data.tuner.Tuner
import com.paricheh.metronome.tuner.ui.tuner.TunerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tunerModule = module {
    single<PitchDetector> { YinPitchDetector() }
    single<FrequencyNormalizer> { SimpleFrequencyNormalizer() }
    single<Tuner> { ChromaticTuner() }

    single<TunerRepository> { TunerRepositoryImpl(get(), get(), get(), get()) }
    viewModelOf(::TunerViewModel)
}
