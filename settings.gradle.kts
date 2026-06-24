rootProject.name = "Metronome"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.myket.ir/")
        }

        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.myket.ir/")
        }

        google()
        mavenCentral()
    }
}

include(":shared")
include(":androidApp")