buildscript {
    repositories {
        jcenter()
        google()
    }

    val kotlinVersion: String by extra("1.3.72")
    val kotlin_version by extra("1.3.72")

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath("com.novoda:bintray-release:0.8.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        jcenter()
        google()
    }
}
