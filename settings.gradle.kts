pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("com.android.application") version "8.6.0"
        id("org.jetbrains.kotlin.android") version "1.9.24"
        id("com.google.dagger.hilt.android") version "2.51"
        kotlin("kapt") version "1.9.24"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TravelCopilot"
include(":app")