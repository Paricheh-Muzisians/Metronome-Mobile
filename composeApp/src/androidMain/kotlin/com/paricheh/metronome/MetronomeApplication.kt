package com.paricheh.metronome

import android.app.Application
import com.paricheh.metronome.di.metronomeModule
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
                metronomeModule
            )
        }
    }
}