package com.paricheh.metronome.tuner.ui.tuner.component

import androidx.compose.ui.graphics.Color
import com.paricheh.metronome.tuner.data.theory.TuningStatus
import kotlin.math.abs

// Configurable thresholds in cents
const val THRESHOLD_PERFECT = 3f
const val THRESHOLD_WARNING = 10f

fun getTuningStatus(cents: Float): TuningStatus {
    val absoluteCents = abs(cents)
    return when {
        absoluteCents <= THRESHOLD_PERFECT -> TuningStatus.Perfect
        absoluteCents <= THRESHOLD_WARNING -> TuningStatus.Warning
        else -> TuningStatus.Bad
    }
}


// Premium Dark Theme Colors
val ColorBackground = Color(0xFF121212)
val ColorSurface = Color(0xFF1E1E1E)
val ColorTextPrimary = Color(0xFFFFFFFF)
val ColorTextSecondary = Color(0xFFA0A0A0)
val ColorGridLine = Color(0xFF333333)

// Status Colors optimized for dark mode contrast
val ColorPerfect = Color(0xFF00E676) // Vibrant Green
val ColorWarning = Color(0xFFFFD54F) // Warm Amber
val ColorBad = Color(0xFFFF5252)     // Soft Red

fun TuningStatus.toColor(): Color = when (this) {
    TuningStatus.Perfect -> ColorPerfect
    TuningStatus.Warning -> ColorWarning
    TuningStatus.Bad -> ColorBad
}

fun getStatusLabel(cents: Float, status: TuningStatus): String {
    if (status == TuningStatus.Perfect) return "Perfectly in tune"
    val direction = if (cents > 0) "sharp" else "flat"
    return when (status) {
        TuningStatus.Warning -> "Slightly $direction"
        TuningStatus.Bad -> if (abs(cents) > 40) "Retune" else "Too $direction"
        else -> ""
    }
}