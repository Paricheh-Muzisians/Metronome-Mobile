package com.paricheh.metronome.tuner.ui.tuner

class IosPermissionChecker : PermissionChecker {
    override fun isAudioPermissionGranted(): Boolean {
        return true
    }
}
