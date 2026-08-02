package com.paricheh.metronome.tuner.di

import com.paricheh.metronome.core.audio.AndroidAudioEngine
import com.paricheh.metronome.core.audio.AudioEngine
import org.koin.dsl.module

val tunerPlatformModule = module {
    single<AudioEngine> { AndroidAudioEngine() }
}
