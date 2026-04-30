package com.paricheh.metronome.di

import com.paricheh.metronome.metronome.MetronomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val metronomeModule = module {
    viewModelOf(::MetronomeViewModel)
}