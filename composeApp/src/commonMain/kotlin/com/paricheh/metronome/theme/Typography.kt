package com.paricheh.metronome.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.toFontFamily
import metronome.composeapp.generated.resources.Res
import metronome.composeapp.generated.resources.dimatype_header
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource

@Composable
fun headerFont() = Font(
    Res.font.dimatype_header
).toFontFamily()