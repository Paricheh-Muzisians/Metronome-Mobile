package com.paricheh.metronome.tuner.di

import com.paricheh.metronome.core.audio.AndroidAudioEngine
import com.paricheh.metronome.core.audio.AudioEngine
import com.paricheh.metronome.core.soundplayer.AndroidTunerSoundPlayer
import com.paricheh.metronome.core.soundplayer.TunerSoundPlayer
import com.paricheh.metronome.tuner.data.preferences.TunerPreferences
import com.paricheh.metronome.tuner.preferences.TunerPreferencesImpl
import com.paricheh.metronome.tuner.ui.tuner.AndroidPermissionChecker
import com.paricheh.metronome.tuner.ui.tuner.PermissionChecker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val tunerPlatformModule = module {
    single<AudioEngine> { AndroidAudioEngine() }
    single<TunerPreferences> { TunerPreferencesImpl(androidContext()) }
    single<TunerSoundPlayer> { AndroidTunerSoundPlayer(androidContext()) }
    single<PermissionChecker> { AndroidPermissionChecker(androidContext()) }
}
