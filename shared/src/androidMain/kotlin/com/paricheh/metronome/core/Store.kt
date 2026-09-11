package com.paricheh.metronome.core

enum class Store {
    CafeBazaar,
    Myket;

    companion object {
        fun getCurrentStore() = Myket
    }
}
