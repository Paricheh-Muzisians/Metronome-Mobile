package com.paricheh.metronome.tuner.di

import com.paricheh.metronome.core.audio.AudioEngine
import com.paricheh.metronome.core.audio.IosAudioEngine
import org.koin.dsl.module

val tunerPlatformModule = module {
    single<AudioEngine> { IosAudioEngine() }
}
