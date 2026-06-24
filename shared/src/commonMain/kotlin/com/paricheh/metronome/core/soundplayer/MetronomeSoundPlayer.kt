package com.paricheh.metronome.core.soundplayer

/**
 * Platform-specific sound player for metronome ticks.
 * Plays two different tones: normal tick and accented tick.
 */
interface MetronomeSoundPlayer {
    fun playTick()
    fun playAccent()
    fun playSubBeat()
    fun release()
}