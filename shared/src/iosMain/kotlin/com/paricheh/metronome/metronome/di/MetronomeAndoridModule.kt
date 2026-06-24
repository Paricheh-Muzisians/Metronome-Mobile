package com.paricheh.metronome.metronome.di

import com.paricheh.metronome.core.soundplayer.IosMetronomeSoundPlayer
import com.paricheh.metronome.core.soundplayer.MetronomeSoundPlayer
import com.paricheh.metronome.core.vibrator.IosMetronomeVibrator
import com.paricheh.metronome.core.vibrator.MetronomeVibrator
import com.paricheh.metronome.metronome.data.IosMetronomeSettings
import com.paricheh.metronome.metronome.data.MetronomeSettings
import org.koin.dsl.module

val metronomeIosModule = module {
    single<MetronomeSoundPlayer> { IosMetronomeSoundPlayer() }
    single<MetronomeVibrator> { IosMetronomeVibrator() }
    single<MetronomeSettings> { IosMetronomeSettings() }
}