package com.paricheh.metronome.rating.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paricheh.metronome.rating.data.repository.RatingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RatingViewModel(
    private val ratingRepository: RatingRepository,
) : ViewModel() {

    val shouldShowRating = ratingRepository.observeShouldShowRating()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    fun markAsPrompt() {
        viewModelScope.launch {
            ratingRepository.markAsPrompt()
        }
    }

    fun markAsRated() {
        viewModelScope.launch {
            ratingRepository.markAsRated()
        }
    }
}