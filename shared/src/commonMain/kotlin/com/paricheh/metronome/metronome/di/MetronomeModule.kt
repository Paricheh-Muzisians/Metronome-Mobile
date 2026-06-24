package com.paricheh.metronome.metronome.di

import com.paricheh.metronome.metronome.ui.metronome.MetronomeViewModel
import com.paricheh.metronome.metronome.ui.setting.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val metronomeSharedModule = module {
    viewModelOf(::MetronomeViewModel)
    viewModelOf(::SettingsViewModel)
}