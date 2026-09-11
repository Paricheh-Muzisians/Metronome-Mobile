package com.paricheh.metronome.rating.di

import com.paricheh.metronome.rating.data.repository.RatingRepository
import com.paricheh.metronome.rating.data.repository.RatingRepositoryImpl
import com.paricheh.metronome.rating.ui.RatingViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val ratingSharedModule = module {
    factoryOf(::RatingRepositoryImpl) bind RatingRepository::class
    viewModelOf(::RatingViewModel)
}
