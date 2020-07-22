@file:JvmMultifileClass

private const val kotlinVersion = "1.3.72"

object BuildPlugin {
    object Classpath {
        const val gradle = "com.android.tools.build:gradle:4.0.1"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }
}

object Dependency {

    object AndroidX {
        const val material = "com.google.android.material:material:1.1.0"
        const val fragment = "androidx.fragment:fragment-ktx:1.2.5"
    }

    object Kotlin {
        const val std = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    }
}
