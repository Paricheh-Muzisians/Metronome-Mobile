package com.paricheh.metronome.metronome.di

import android.content.pm.ApplicationInfo
import com.paricheh.metronome.core.analytics.AnalyticsManager
import com.paricheh.metronome.core.analytics.CloudAnalyticsManager
import com.paricheh.metronome.core.analytics.StubAnalyticsManager
import com.paricheh.metronome.core.soundplayer.AndroidMetronomeSoundPlayer
import com.paricheh.metronome.core.soundplayer.MetronomeSoundPlayer
import com.paricheh.metronome.core.vibrator.AndroidMetronomeVibrator
import com.paricheh.metronome.core.vibrator.MetronomeVibrator
import com.paricheh.metronome.metronome.data.AndroidMetronomeSettings
import com.paricheh.metronome.metronome.data.MetronomeSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val metronomeAndroidModule = module {
    single<MetronomeSoundPlayer> { AndroidMetronomeSoundPlayer(get()) }
    single<MetronomeVibrator> { AndroidMetronomeVibrator(get()) }
    single<MetronomeSettings> { AndroidMetronomeSettings(get()) }

    single<AnalyticsManager> {
        val context = androidContext()
        val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebug) {
            StubAnalyticsManager()
        } else {
            CloudAnalyticsManager()
        }
    }
}
