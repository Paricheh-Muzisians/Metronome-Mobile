package com.paricheh.metronome.core.soundplayer

import com.paricheh.metronome.tuner.model.NoteInfo

interface TunerSoundPlayer {
    fun playGuitarSample(note: NoteInfo)
}