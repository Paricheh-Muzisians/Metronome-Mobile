package com.paricheh.metronome.di

import com.paricheh.metronome.core.soundplayer.MetronomeSoundPlayer
import com.paricheh.metronome.core.vibrator.MetronomeVibrator
import org.koin.dsl.module

val androidModule = module {
    single { MetronomeSoundPlayer(get()) }
    single { MetronomeVibrator(get()) }
}
