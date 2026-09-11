package com.paricheh.metronome

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paricheh.metronome.designsystem.MetronomeTheme
import com.paricheh.metronome.metronome.ui.metronome.MetronomeScreen
import com.paricheh.metronome.metronome.ui.setting.SettingsScreen
import com.paricheh.metronome.navigation.MetronomeScreens.Metronome
import com.paricheh.metronome.navigation.MetronomeScreens.Setting
import com.paricheh.metronome.rating.ui.Rating

@Composable
fun App() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MetronomeTheme {
            val navController = rememberNavController()
            var blurEffectRatio by rememberSaveable { mutableStateOf(0f) }
            val animatedBlurEffect by animateFloatAsState(blurEffectRatio)

            Rating(
                onRatingVisibilityChange = {
                    blurEffectRatio = if (it) {
                        6f
                    } else {
                        0f
                    }
                }
            )

            NavHost(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxSize()
                    .blur(animatedBlurEffect.dp),
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
}
