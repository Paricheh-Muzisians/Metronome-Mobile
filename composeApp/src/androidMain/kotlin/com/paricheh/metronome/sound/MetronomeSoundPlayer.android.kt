package com.paricheh.metronome.sound

import android.media.AudioAttributes
import android.media.SoundPool
import com.paricheh.metronome.R
import android.content.Context


actual class MetronomeSoundPlayer(context: Context) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        .build()

    private var normalTickId: Int = 0
    private var accentTickId: Int = 0
    private var isLoaded = false

    init {
        // Load sounds from resources
        // We'll add the audio files to res/raw/
        normalTickId = soundPool.load(context, R.raw.tick_normal, 1)
        accentTickId = soundPool.load(context, R.raw.tick_accent, 1)

        // Wait for sounds to load
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isLoaded = true
            }
        }
    }

    actual fun playTick(isAccent: Boolean) {
        if (!isLoaded) return

        val soundId = if (isAccent) accentTickId else normalTickId
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    actual fun release() {
        soundPool.release()

    }
}
