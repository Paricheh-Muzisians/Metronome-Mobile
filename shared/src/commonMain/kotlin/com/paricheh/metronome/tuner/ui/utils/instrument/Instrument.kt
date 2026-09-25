package com.paricheh.metronome.tuner.ui.utils.instrument

import com.paricheh.metronome.tuner.model.NoteInfo
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.eighteen_eight_key_piano
import metronome.shared.generated.resources.guitar
import metronome.shared.generated.resources.piano
import metronome.shared.generated.resources.setar
import metronome.shared.generated.resources.standard

sealed interface Instrument {
    val key: String
    val notes: List<NoteInfo>
}

fun Instrument.getTitle() = when (this) {
    is Guitar6String -> {
        Res.string.guitar
    }

    is Piano88 -> {
        Res.string.piano
    }

    is Setar -> {
        Res.string.setar
    }
}

fun Instrument.getTypeText() = when (this) {
    is Guitar6String -> {
        Res.string.standard
    }

    is Piano88 -> {
        Res.string.eighteen_eight_key_piano
    }

    is Setar -> {
        Res.string.standard
    }
}