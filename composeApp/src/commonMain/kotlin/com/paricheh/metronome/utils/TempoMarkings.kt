package com.paricheh.metronome.utils

import metronome.composeapp.generated.resources.Res
import metronome.composeapp.generated.resources.english_tempo_adagio
import metronome.composeapp.generated.resources.english_tempo_allegro
import metronome.composeapp.generated.resources.english_tempo_andante
import metronome.composeapp.generated.resources.english_tempo_grave
import metronome.composeapp.generated.resources.english_tempo_larghetto
import metronome.composeapp.generated.resources.english_tempo_larghissimo
import metronome.composeapp.generated.resources.english_tempo_largo
import metronome.composeapp.generated.resources.english_tempo_lento
import metronome.composeapp.generated.resources.english_tempo_moderato
import metronome.composeapp.generated.resources.english_tempo_prestissimo
import metronome.composeapp.generated.resources.english_tempo_presto
import metronome.composeapp.generated.resources.english_tempo_vivace
import metronome.composeapp.generated.resources.persian_tempo_adagio
import metronome.composeapp.generated.resources.persian_tempo_allegro
import metronome.composeapp.generated.resources.persian_tempo_andante
import metronome.composeapp.generated.resources.persian_tempo_grave
import metronome.composeapp.generated.resources.persian_tempo_larghetto
import metronome.composeapp.generated.resources.persian_tempo_larghissimo
import metronome.composeapp.generated.resources.persian_tempo_largo
import metronome.composeapp.generated.resources.persian_tempo_lento
import metronome.composeapp.generated.resources.persian_tempo_moderato
import metronome.composeapp.generated.resources.persian_tempo_prestissimo
import metronome.composeapp.generated.resources.persian_tempo_presto
import metronome.composeapp.generated.resources.persian_tempo_vivace
import org.jetbrains.compose.resources.StringResource

enum class TempoMarkings {
    PRESTISSIMO,
    PRESTO,
    VIVACE,
    ALLEGRO,
    MODERATO,
    ANDANTE,
    ADAGIO,
    LARGHETTO,
    LENTO,
    LARGO,
    GRAVE,
    LARGHISSIMO,
}

fun TempoMarkings.bpmRange(): IntRange = when (this) {
    TempoMarkings.PRESTISSIMO -> 200..240
    TempoMarkings.PRESTO -> 168..200
    TempoMarkings.VIVACE -> 140..168
    TempoMarkings.ALLEGRO -> 120..140
    TempoMarkings.MODERATO -> 108..120
    TempoMarkings.ANDANTE -> 76..108
    TempoMarkings.ADAGIO -> 66..76
    TempoMarkings.LARGHETTO -> 60..66
    TempoMarkings.LENTO -> 45..60
    TempoMarkings.LARGO -> 40..45
    TempoMarkings.GRAVE -> 25..40
    TempoMarkings.LARGHISSIMO -> 20..25
}

fun TempoMarkings.titleEnglish(): StringResource = when (this) {
    TempoMarkings.PRESTISSIMO -> Res.string.english_tempo_prestissimo
    TempoMarkings.PRESTO -> Res.string.english_tempo_presto
    TempoMarkings.VIVACE -> Res.string.english_tempo_vivace
    TempoMarkings.ALLEGRO -> Res.string.english_tempo_allegro
    TempoMarkings.MODERATO -> Res.string.english_tempo_moderato
    TempoMarkings.ANDANTE -> Res.string.english_tempo_andante
    TempoMarkings.ADAGIO -> Res.string.english_tempo_adagio
    TempoMarkings.LARGHETTO -> Res.string.english_tempo_larghetto
    TempoMarkings.LENTO -> Res.string.english_tempo_lento
    TempoMarkings.LARGO -> Res.string.english_tempo_largo
    TempoMarkings.GRAVE -> Res.string.english_tempo_grave
    TempoMarkings.LARGHISSIMO -> Res.string.english_tempo_larghissimo
}

fun TempoMarkings.titlePersian(): StringResource = when (this) {
    TempoMarkings.PRESTISSIMO -> Res.string.persian_tempo_prestissimo
    TempoMarkings.PRESTO -> Res.string.persian_tempo_presto
    TempoMarkings.VIVACE -> Res.string.persian_tempo_vivace
    TempoMarkings.ALLEGRO -> Res.string.persian_tempo_allegro
    TempoMarkings.MODERATO -> Res.string.persian_tempo_moderato
    TempoMarkings.ANDANTE -> Res.string.persian_tempo_andante
    TempoMarkings.ADAGIO -> Res.string.persian_tempo_adagio
    TempoMarkings.LARGHETTO -> Res.string.persian_tempo_larghetto
    TempoMarkings.LENTO -> Res.string.persian_tempo_lento
    TempoMarkings.LARGO -> Res.string.persian_tempo_largo
    TempoMarkings.GRAVE -> Res.string.persian_tempo_grave
    TempoMarkings.LARGHISSIMO -> Res.string.persian_tempo_larghissimo
}

fun getTempoMarkingByBpm(bpm: Int): TempoMarkings =
    TempoMarkings.entries.firstOrNull { bpm in it.bpmRange() }
        ?: error("out of Range BPM")

fun TempoMarkings.accurateBpm(): Int {
    val range = bpmRange()
    return (range.first + range.last) / 2
}