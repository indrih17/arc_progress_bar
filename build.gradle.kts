buildscript {
    repositories {
        jcenter()
        google()
    }

    dependencies {
        classpath(BuildPlugin.Classpath.gradle)
        classpath(BuildPlugin.Classpath.kotlin)
    }
}

allprojects {
    repositories {
        jcenter()
        google()
        jcenter()
        mavenCentral()
    }
}
