import groovy.lang.MissingPropertyException
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.maven
import java.io.File
import java.util.Properties

fun Project.requireProperty(name: String) =
    findProperty(name)?.toString() ?: throw MissingPropertyException("Not found property with name: $name")

fun Project.localProperties(): Properties {
    val local = Properties()
    val localProperties: File = rootProject.file("local.properties")
    if (localProperties.exists()) {
        localProperties.inputStream().use { local.load(it) }
    }
    return local
}

fun PublishingExtension.configure(
    project: Project,
    bintrayOrg: String,
    bintrayRepo: String,
    groupId: String,
    artifactId: String,
    versionName: String
) {
    repositories.maven(
        url = "https://api.bintray.com/maven/$bintrayOrg/$bintrayRepo/$artifactId/;publish=1"
    ) {
        name = "bintray"
        credentials {
            val localProperties = project.localProperties()
            username = localProperties.getProperty("publication.user.login")
            password = localProperties.getProperty("publication.user.password")
        }
    }

    publications {
        register("mavenPublish", MavenPublication::class.java) {
            this.groupId = groupId
            this.artifactId = artifactId
            this.version = versionName

            pom {
                name.set(groupId)

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("indrih17")
                        name.set("Kirill Indrih")
                    }
                }
            }
        }
    }
}

