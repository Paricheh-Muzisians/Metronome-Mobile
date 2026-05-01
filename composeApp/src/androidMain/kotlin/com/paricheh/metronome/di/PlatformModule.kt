package com.paricheh.metronome.di

import com.paricheh.metronome.sound.MetronomeSoundPlayer
import org.koin.dsl.module

val androidModule = module {
    single { MetronomeSoundPlayer(get()) }
}
