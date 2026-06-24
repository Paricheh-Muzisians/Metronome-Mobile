package com.paricheh.metronome.metronome.di

import com.paricheh.metronome.core.soundplayer.AndroidMetronomeSoundPlayer
import com.paricheh.metronome.core.soundplayer.MetronomeSoundPlayer
import com.paricheh.metronome.core.vibrator.AndroidMetronomeVibrator
import com.paricheh.metronome.core.vibrator.MetronomeVibrator
import com.paricheh.metronome.metronome.data.AndroidMetronomeSettings
import com.paricheh.metronome.metronome.data.MetronomeSettings
import org.koin.dsl.module

val metronomeAndroidModule = module {
    single<MetronomeSoundPlayer> { AndroidMetronomeSoundPlayer(get()) }
    single<MetronomeVibrator> { AndroidMetronomeVibrator(get()) }
    single<MetronomeSettings> { AndroidMetronomeSettings(get()) }
}