package com.paricheh.metronome

import android.app.Application
import com.paricheh.metronome.metronome.di.metronomeAndroidModule
import com.paricheh.metronome.metronome.di.metronomeSharedModule
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

private const val APP_METRICA_API_KEY = "a54d3d4e-42ec-4a32-a717-1a23b75d9d2c"

class MetronomeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MetronomeApplication)
            modules(metronomeAndroidModule, metronomeSharedModule)
        }

        initiateMetrica()
    }

    private fun initiateMetrica() {
        val config = AppMetricaConfig
            .newConfigBuilder(APP_METRICA_API_KEY)
            .withSessionsAutoTrackingEnabled(true)
            .withSessionTimeout(60)
            .withCrashReporting(true)
            .withLocationTracking(false)
            .build()
        AppMetrica.activate(this, config)
    }
}
