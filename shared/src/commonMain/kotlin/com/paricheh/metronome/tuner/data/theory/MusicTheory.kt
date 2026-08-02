package com.paricheh.metronome.tuner.data.theory

import org.jetbrains.compose.resources.StringResource
import metronome.shared.generated.resources.Res
import metronome.shared.generated.resources.*
import kotlin.math.pow

/**
 * Represents a musical note using Solfège notation (Do, Re, Mi...).
 */
enum class MusicalNote(val displayName: String) {
    Do("Do"),
    DoSharp("Do#"),
    Re("Re"),
    ReSharp("Re#"),
    Mi("Mi"),
    Fa("Fa"),
    FaSharp("Fa#"),
    Sol("Sol"),
    SolSharp("Sol#"),
    La("La"),
    LaSharp("La#"),
    Si("Si");

    fun persianNameRes(): StringResource = when (this) {
        Do -> Res.string.note_do
        DoSharp -> Res.string.note_do_sharp
        Re -> Res.string.note_re
        ReSharp -> Res.string.note_re_sharp
        Mi -> Res.string.note_mi
        Fa -> Res.string.note_fa
        FaSharp -> Res.string.note_fa_sharp
        Sol -> Res.string.note_sol
        SolSharp -> Res.string.note_sol_sharp
        La -> Res.string.note_la
        LaSharp -> Res.string.note_la_sharp
        Si -> Res.string.note_si
    }
}

/**
 * Represents the status of tuning for a detected note.
 */
enum class TuningStatus {
    Perfect,
    Warning,
    Bad
}

/**
 * Interface for a musical instrument providing its tuning information.
 */
interface Instrument {
    val name: String
    val notes: List<NoteInfo>
}

/**
 * Information about a specific note on an instrument.
 */
data class NoteInfo(
    val note: MusicalNote,
    val octave: Int,
    val frequency: Float
)

/**
 * Interface for a musical temperament (tuning system).
 */
interface Temperament {
    val name: String
    fun getFrequency(note: MusicalNote, octave: Int): Float
}

/**
 * Implementation of Equal Temperament tuning system.
 */
class EqualTemperament : Temperament {
    override val name: String = "Equal Temperament"

    override fun getFrequency(note: MusicalNote, octave: Int): Float {
        // A4 = 440Hz
        // Frequency = 440 * 2^((n - 57) / 12) where n is the MIDI note number
        // MIDI note 69 is A4 (La4)
        val midiNote = getMidiNote(note, octave)
        return (440.0 * 2.0.pow((midiNote - 69.0) / 12.0)).toFloat()
    }

    private fun getMidiNote(note: MusicalNote, octave: Int): Int {
        val noteIndex = note.ordinal
        // Standard convention: C4 is MIDI 60.
        // Do is index 0. 0 + (4+1)*12 = 60.
        return noteIndex + (octave + 1) * 12
    }
}

/**
 * Implementation of an 88-key Piano instrument.
 */
class Piano88(private val temperament: Temperament) : Instrument {
    override val name: String = "Piano (88 Keys)"
    
    override val notes: List<NoteInfo> by lazy {
        val allNotes = mutableListOf<NoteInfo>()
        // Piano 88 keys: A0 (21) to C8 (108)
        for (midi in 21..108) {
            val noteIndex = midi % 12
            val octave = (midi / 12) - 1
            val note = MusicalNote.entries[noteIndex]
            allNotes.add(NoteInfo(note, octave, temperament.getFrequency(note, octave)))
        }
        allNotes
    }
}
