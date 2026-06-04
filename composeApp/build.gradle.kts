import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.androidx.datastore.preferences)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.compose.animation)
            implementation(libs.compose.materialIcons)
            implementation(libs.compose.runtimeSavable)
            implementation(libs.compose.uiUtils)
            implementation(libs.compose.navigation)

            implementation(libs.koin.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.viewmodel)
            implementation(libs.koin.viewmodelNavigation)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlin.serialization)
            implementation(libs.setting)
            implementation(libs.unstyled)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.paricheh.metronome"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    signingConfigs {
        create("release") {
            enableV3Signing = true
            storeFile = rootProject.file("release/paricheh-metronome.jks")
            keyAlias = loadValueFromProperties("KEY_ALIAS")
            storePassword = loadValueFromProperties("STORE_PASSWORD")
            keyPassword = loadValueFromProperties("KEY_PASSWORD")
        }
    }

    defaultConfig {
        applicationId = "com.paricheh.metronome"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 10100010
        versionName = "1.0.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            isDebuggable = true
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

fun loadValueFromProperties(key: String, propName: String = "local.properties"): String? {
    val properties = Properties()
    val localPropertiesFile = rootProject.file(propName)
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use {
            properties.load(it)
        }
    }
    return properties.getProperty(key)
}
