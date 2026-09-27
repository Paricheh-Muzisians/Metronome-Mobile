package com.paricheh.metronome.tuner.ui.utils.instrument

import com.paricheh.metronome.tuner.model.NoteInfo
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.eighteen_eight_key_piano
import metronome.shared.generated.resources.guitar
import metronome.shared.generated.resources.mahoor
import metronome.shared.generated.resources.piano
import metronome.shared.generated.resources.setar
import metronome.shared.generated.resources.shur_do
import metronome.shared.generated.resources.shur_re
import metronome.shared.generated.resources.shur_sol
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

    is SetarMahoor -> {
        Res.string.setar
    }

    is SetarShurDo -> {
        Res.string.setar
    }

    is SetarShurRe -> {
        Res.string.setar
    }

    is SetarShurSol -> {
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

    is SetarMahoor -> {
        Res.string.mahoor
    }

    is SetarShurDo -> {
        Res.string.shur_do
    }

    is SetarShurRe -> {
        Res.string.shur_re
    }

    is SetarShurSol -> {
        Res.string.shur_sol
    }
}