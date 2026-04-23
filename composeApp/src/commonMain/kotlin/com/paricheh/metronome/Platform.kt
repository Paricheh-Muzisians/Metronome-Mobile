package com.paricheh.metronome

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform