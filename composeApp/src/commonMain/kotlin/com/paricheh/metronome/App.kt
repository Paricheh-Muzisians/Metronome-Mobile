package com.paricheh.metronome

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paricheh.metronome.metronome.MetronomeScreen
import com.paricheh.metronome.navigation.MetronomeScreens.Metronome
import com.paricheh.metronome.navigation.MetronomeScreens.Setting
import com.paricheh.metronome.settings.SettingsScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = Metronome
        ) {
            composable<Metronome>(
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                MetronomeScreen(navController)
            }
            composable<Setting>(
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                SettingsScreen(navController)
            }
        }
    }
}