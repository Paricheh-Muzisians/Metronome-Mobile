package com.paricheh.metronome.core

import kotlin.time.Clock

actual fun getCurrentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}
