package com.paricheh.metronome.di

import com.paricheh.metronome.createMetronomeSettings
import com.paricheh.metronome.metronome.MetronomeViewModel
import com.paricheh.metronome.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val metronomeModule = module {
    viewModelOf(::MetronomeViewModel)
    single { createMetronomeSettings() }
    viewModel { SettingsViewModel(get()) }
}