package com.paricheh.metronome.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

@Composable
fun MetronomeTheme(
    typography: Typography = metronomeTypography(),
    colors: ColorScheme = metronomeColors,
    shapes: Shapes = MaterialTheme.shapes,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        typography = typography,
        colorScheme = colors,
        shapes = shapes,
        content = content
    )
}
