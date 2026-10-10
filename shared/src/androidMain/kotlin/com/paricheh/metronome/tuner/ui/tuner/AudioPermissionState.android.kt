package com.paricheh.metronome.tuner.ui.tuner

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class AndroidAudioPermissionState(
    private val launchRequest: () -> Unit,
) : AudioPermissionState {
    override fun requestPermission() {
        launchRequest()
    }
}

@Composable
actual fun rememberAudioPermissionState(
    onPermissionResult: (granted: Boolean) -> Unit
): AudioPermissionState {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        onPermissionResult(granted)
    }

    return remember(launcher) {
        AndroidAudioPermissionState(
            launchRequest = {
                launcher.launch(Manifest.permission.RECORD_AUDIO)
            }
        )
    }
}
