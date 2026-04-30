package com.paricheh.metronome

import android.app.Application
import com.paricheh.metronome.di.metronomeModule
import com.paricheh.metronome.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MetronomeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeMetronomeSettings(this)
        startKoin {
            androidLogger()
            androidContext(this@MetronomeApplication)

            modules(metronomeModule)
            modules(settingsModule)
        }
    }
}

val settingsModule = module {
    single { createMetronomeSettings() }
    viewModel { SettingsViewModel(get()) }
}
