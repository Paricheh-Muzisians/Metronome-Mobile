package com.paricheh.metronome

import android.app.Application
import com.paricheh.metronome.metronome.di.metronomeAndroidModule
import com.paricheh.metronome.metronome.di.metronomeSharedModule
import com.paricheh.metronome.tuner.di.tunerModule
import com.paricheh.metronome.tuner.di.tunerPlatformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MetronomeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MetronomeApplication)
            modules(
                metronomeAndroidModule,
                metronomeSharedModule,
                tunerModule,
                tunerPlatformModule
            )
        }
    }
}
