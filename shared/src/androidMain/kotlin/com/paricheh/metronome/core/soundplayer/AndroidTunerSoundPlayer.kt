package com.paricheh.metronome.core.soundplayer

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.paricheh.metronome.shared.R
import com.paricheh.metronome.tuner.model.MusicalNote
import com.paricheh.metronome.tuner.model.NoteInfo
import com.paricheh.metronome.tuner.ui.utils.instrument.Guitar6String

class AndroidTunerSoundPlayer(
    context: Context,
) : TunerSoundPlayer {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(20)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .build()
        )
        .build()

    private val samples = mutableMapOf<NoteInfo, Int>()

    private var isLoaded = false

    init {
        // Load sounds from resources
        // We'll add the audio files to res/raw/
        val guitar6String = Guitar6String().notes.associateWith {
            when (it.note) {
                MusicalNote.Mi if it.octave == 2 -> {
                    soundPool.load(context, R.raw.guitar_mi2, 1)
                }

                MusicalNote.Mi if it.octave == 4 -> {
                    soundPool.load(context, R.raw.guitar_mi4, 1)
                }

                MusicalNote.La if it.octave == 2 -> {
                    soundPool.load(context, R.raw.guitar_la2, 1)
                }

                MusicalNote.Re if it.octave == 3 -> {
                    soundPool.load(context, R.raw.guitar_re3, 1)
                }

                MusicalNote.Si if it.octave == 3 -> {
                    soundPool.load(context, R.raw.guitar_si3, 1)
                }

                MusicalNote.Sol if it.octave == 3 -> {
                    soundPool.load(context, R.raw.guitar_sol3, 1)
                }

                else -> error("what kind of guitar are you using")
            }
        }

        samples.putAll(guitar6String)

        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isLoaded = true
            }
        }
    }


    override fun playGuitarSample(note: NoteInfo) {
        if (!isLoaded) return

        samples[note]?.let {
            soundPool.play(it, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }
}