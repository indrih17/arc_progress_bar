@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    id("maven-publish")
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
    implementation(project(":core"))
    implementation(Dependency.AndroidX.fragment)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = AndroidProject.jvmTarget
    }
}

val publicationGroupId: String = project.requireProperty(name = "publication.groupId")
val publicationVersionName: String = project.requireProperty(name = "publication.arc.versionName")

group = publicationGroupId
version = publicationVersionName

publishing {
    configure(
        project = project,
        bintrayOrg = project.requireProperty("publication.bintray.org"),
        bintrayRepo = project.requireProperty(name = "publication.bintray.repo"),
        groupId = publicationGroupId,
        artifactId = project.requireProperty(name = "publication.arc.artifactId"),
        versionName = publicationVersionName
    )
}
