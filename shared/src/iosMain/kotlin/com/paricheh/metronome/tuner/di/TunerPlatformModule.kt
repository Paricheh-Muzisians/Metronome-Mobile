package com.paricheh.metronome.tuner.di

import com.paricheh.metronome.core.audio.AudioEngine
import com.paricheh.metronome.core.audio.IosAudioEngine
import com.paricheh.metronome.core.soundplayer.IosTunerSoundPlayer
import com.paricheh.metronome.core.soundplayer.TunerSoundPlayer
import com.paricheh.metronome.tuner.data.preferences.TunerPreferences
import com.paricheh.metronome.tuner.preferences.IosTunerPreferences
import com.paricheh.metronome.tuner.ui.tuner.IosPermissionChecker
import com.paricheh.metronome.tuner.ui.tuner.PermissionChecker
import org.koin.dsl.module

val tunerPlatformModule = module {
    single<AudioEngine> { IosAudioEngine() }
    single<TunerPreferences> { IosTunerPreferences() }
    single<TunerSoundPlayer> { IosTunerSoundPlayer() }
    single<PermissionChecker> { IosPermissionChecker() }
}
