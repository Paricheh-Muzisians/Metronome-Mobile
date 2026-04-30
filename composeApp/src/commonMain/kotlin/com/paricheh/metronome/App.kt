package com.paricheh.metronome

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.paricheh.metronome.metronome.MetronomeScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        MetronomeScreen()
    }
}