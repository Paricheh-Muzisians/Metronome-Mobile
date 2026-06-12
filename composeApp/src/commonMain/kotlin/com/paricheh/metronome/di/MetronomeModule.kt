package com.paricheh.metronome.di

import com.paricheh.metronome.createMetronomeSettings
import com.paricheh.metronome.metronome.MetronomeViewModel
import com.paricheh.metronome.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val metronomeModule = module {
    viewModel { MetronomeViewModel(get(), get(), get()) }
    single { createMetronomeSettings() }
    viewModel { SettingsViewModel(get(), get()) }
}