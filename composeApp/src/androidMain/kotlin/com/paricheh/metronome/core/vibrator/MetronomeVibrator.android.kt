package com.paricheh.metronome.core.vibrator

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

actual class MetronomeVibrator(context: Context) {
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private fun vibratePredefined(effectId: Int, fallbackDuration: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(effectId))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    fallbackDuration,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(fallbackDuration)
        }
    }

    actual fun vibrateOnButtonClick() {
        vibratePredefined(VibrationEffect.EFFECT_CLICK, 50)
    }

    actual fun vibrateOnRadioButtonChange() {
        vibratePredefined(VibrationEffect.EFFECT_TICK, 20)
    }

    actual fun vibrateOnScroll() {
        vibratePredefined(VibrationEffect.EFFECT_TICK, 10)
    }

    actual fun vibrateOnTick() {
        vibratePredefined(VibrationEffect.EFFECT_HEAVY_CLICK, 15)
    }
}
