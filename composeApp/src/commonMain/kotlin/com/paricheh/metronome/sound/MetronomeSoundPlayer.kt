package com.paricheh.metronome.sound

/**
 * Platform-specific sound player for metronome ticks.
 * Plays two different tones: normal tick and accented tick.
 */
expect class MetronomeSoundPlayer {
    fun playTick(isAccent: Boolean)
    fun release()
}