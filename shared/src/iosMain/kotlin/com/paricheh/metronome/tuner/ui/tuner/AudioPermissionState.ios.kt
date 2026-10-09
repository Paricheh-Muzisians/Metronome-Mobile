package com.paricheh.metronome.tuner.ui.tuner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class IosAudioPermissionState(
    private val onPermissionResult: (granted: Boolean) -> Unit
) : AudioPermissionState {
    override fun requestPermission() {
        onPermissionResult(true)
    }
}

@Composable
actual fun rememberAudioPermissionState(
    onPermissionResult: (granted: Boolean) -> Unit
): AudioPermissionState {
    return remember(onPermissionResult) {
        IosAudioPermissionState(onPermissionResult)
    }
}
