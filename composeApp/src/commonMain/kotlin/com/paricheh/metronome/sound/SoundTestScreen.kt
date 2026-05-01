package com.paricheh.metronome.sound

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun SoundTestScreen() {
    // Get sound player from Koin
    val soundPlayer: MetronomeSoundPlayer = koinInject()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sound Test",
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { soundPlayer.playTick(false) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Play Normal Tick (800Hz)")
        }

        Button(
            onClick = { soundPlayer.playTick(true) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Play Accent Tick (1200Hz)")
        }
    }
}
