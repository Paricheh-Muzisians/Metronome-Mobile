package com.paricheh.metronome.metronome.di

import com.paricheh.metronome.core.soundplayer.IosMetronomeSoundPlayer
import com.paricheh.metronome.core.soundplayer.MetronomeSoundPlayer
import com.paricheh.metronome.core.vibrator.IosMetronomeVibrator
import com.paricheh.metronome.core.vibrator.MetronomeVibrator
import com.paricheh.metronome.core.platform.IosPlatformActionHandler
import com.paricheh.metronome.core.platform.PlatformActionHandler
import com.paricheh.metronome.metronome.data.IosMetronomeSettings
import com.paricheh.metronome.metronome.data.MetronomeSettings
import com.paricheh.metronome.rating.data.preferences.IosRatingPreferences
import com.paricheh.metronome.rating.data.preferences.RatingPreferences
import com.paricheh.metronome.rating.di.ratingSharedModule
import org.koin.dsl.bind
import org.koin.dsl.module

val metronomeIosModule = module {
    includes(ratingSharedModule)
    single<MetronomeSoundPlayer> { IosMetronomeSoundPlayer() }
    single<MetronomeVibrator> { IosMetronomeVibrator() }
    single<MetronomeSettings> { IosMetronomeSettings() }
    single<PlatformActionHandler> { IosPlatformActionHandler() }

    single { IosRatingPreferences() } bind RatingPreferences::class
}