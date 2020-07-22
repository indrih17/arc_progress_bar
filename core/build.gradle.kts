@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(AndroidProject.compileSdkVersion)
    buildToolsVersion(AndroidProject.buildToolsVersion)

    defaultConfig {
        minSdkVersion(AndroidProject.minSdkVersion)
        targetSdkVersion(AndroidProject.targetSdkVersion)
    }

    sourceSets {
        val main by getting {
            java.srcDirs("src/main/kotlin")
        }
    }
}

dependencies {
    implementation(Dependency.AndroidX.material)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = AndroidProject.jvmTarget
    }
}
