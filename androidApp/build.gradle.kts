import java.util.Properties
import com.android.build.api.dsl.ApplicationDefaultConfig
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.DecimalFormat

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

android {
    namespace = "com.paricheh.metronome"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    ndkVersion = "30.0.14904198"

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

        setVersion(
            epoch = 1,
            major = 1,
            minor = 1,
            patch = 0,
            offset = 0
        )
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
    implementation(project(":shared"))
    implementation(libs.core.ktx)
    debugImplementation(libs.compose.uiTooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
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

fun ApplicationDefaultConfig.setVersion(
    epoch: Int,
    major: Int,
    minor: Int,
    patch: Int,
    offset: Int,
) {
    val versionFormat = DecimalFormat("00")

    val majorFormated = versionFormat.format(major)
    val minorFormated = versionFormat.format(minor)
    val patchFormated = versionFormat.format(patch)

    versionName = "$major.$minor.$patch"
    versionCode = ("$epoch$majorFormated$minorFormated$patchFormated$offset").toInt()
}
