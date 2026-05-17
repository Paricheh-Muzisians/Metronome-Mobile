package com.paricheh.metronome.sound

import android.media.AudioAttributes
import android.media.SoundPool
import com.paricheh.metronome.R
import android.content.Context

actual class MetronomeSoundPlayer(context: Context) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(20)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .build()
        )
        .build()

    private var normalTickId: Int = 0
    private var accentTickId: Int = 0
    private var subBeatTickId: Int = 0

    private var isLoaded = false

    init {
        // Load sounds from resources
        // We'll add the audio files to res/raw/
        normalTickId = soundPool.load(context, R.raw.click_normal, 1)
        accentTickId = soundPool.load(context, R.raw.click_accent, 1)
        subBeatTickId = soundPool.load(context, R.raw.bottle_accent, 1)

        // Wait for sounds to load
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isLoaded = true
            }
        }
    }

    actual fun playTick() {
        if (!isLoaded) return

        soundPool.play(normalTickId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    actual fun release() {
        soundPool.release()
    }

    actual fun playAccent() {
        if (!isLoaded) return

        soundPool.play(accentTickId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    actual fun playSubBeat() {
        if (!isLoaded) return

        soundPool.play(subBeatTickId, 1.0f, 1.0f, 1, 0, 1.0f)
    }
}
