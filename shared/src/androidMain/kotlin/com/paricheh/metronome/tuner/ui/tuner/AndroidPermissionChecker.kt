package com.paricheh.metronome.tuner.ui.tuner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class AndroidPermissionChecker(private val context: Context) : PermissionChecker {
    override fun isAudioPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
}
