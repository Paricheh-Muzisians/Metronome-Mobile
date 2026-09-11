package com.paricheh.metronome

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.paricheh.metronome.core.analytics.AnalyticsManager
import com.paricheh.metronome.core.analytics.LocalAnalyticsManager
import com.paricheh.metronome.core.platform.LocalPlatformActionHandler
import com.paricheh.metronome.core.platform.PlatformActionHandler
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val analyticsManager: AnalyticsManager by inject()
    private val platformActionHandler: PlatformActionHandler by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // we should not let the app screen to turn of, because if activity
        // pause metronome will stop ticking. temporally we are setting
        // this to always keep screen on, but we need to change this to only work
        // when metronome is started.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb())
        )

        setContent {
            CompositionLocalProvider(
                LocalAnalyticsManager provides analyticsManager,
                LocalPlatformActionHandler provides platformActionHandler
            ) {
                App()
            }
        }
    }
}
