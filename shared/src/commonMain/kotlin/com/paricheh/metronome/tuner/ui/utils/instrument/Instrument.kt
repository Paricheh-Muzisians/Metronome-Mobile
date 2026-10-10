package com.paricheh.metronome.tuner.ui.utils.instrument

import com.paricheh.metronome.tuner.model.NoteInfo
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.eighteen_eight_key_piano
import metronome.shared.generated.resources.guitar
import metronome.shared.generated.resources.piano
import metronome.shared.generated.resources.setar
import metronome.shared.generated.resources.setar_abuata
import metronome.shared.generated.resources.setar_bayattork
import metronome.shared.generated.resources.setar_chahargah
import metronome.shared.generated.resources.setar_dashti
import metronome.shared.generated.resources.setar_esfahan
import metronome.shared.generated.resources.setar_homayoun
import metronome.shared.generated.resources.setar_mahoor
import metronome.shared.generated.resources.setar_nava
import metronome.shared.generated.resources.setar_rastpanjgah
import metronome.shared.generated.resources.setar_shur
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

    is SetarMahoor -> {
        Res.string.setar_mahoor
    }

    is SetarShur -> {
        Res.string.setar_shur
    }

    is SetarHomayoun -> {
        Res.string.setar_homayoun
    }

    is SetarChahargah -> {
        Res.string.setar_chahargah
    }

    is SetarRastPanjgah -> {
        Res.string.setar_rastpanjgah
    }

    is SetarNava -> {
        Res.string.setar_nava
    }

    is SetarEsfahan -> {
        Res.string.setar_esfahan
    }

    is SetarAbuata -> {
        Res.string.setar_abuata
    }

    is SetarDashti -> {
        Res.string.setar_dashti
    }

    is SetarBayatTork -> {
        Res.string.setar_bayattork
    }
}
