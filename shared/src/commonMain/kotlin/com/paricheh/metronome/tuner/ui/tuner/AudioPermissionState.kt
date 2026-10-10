package com.paricheh.metronome.tuner.ui.tuner

import androidx.compose.runtime.Composable

interface AudioPermissionState {
    fun requestPermission()
}

@Composable
expect fun rememberAudioPermissionState(
    onPermissionResult: (granted: Boolean) -> Unit = {}
): AudioPermissionState
