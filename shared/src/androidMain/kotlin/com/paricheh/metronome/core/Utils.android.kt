package com.paricheh.metronome.core

actual fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
}
