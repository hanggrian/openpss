import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.kotlin
import java.io.File
import java.nio.file.Files.delete

buildscript {
    repositories {
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots") // required for packr
    }
    dependencies {
        classpath(kotlin("gradle-plugin", VERSION_KOTLIN))
        classpath(hendraanggrian("r", VERSION_R))
        classpath(hendraanggrian("buildconfig", VERSION_BUILDCONFIG))
        classpath(hendraanggrian("packr", VERSION_PACKR))
        classpath(shadow())
        classpath(junitPlatform("gradle-plugin"))
    }
}

allprojects {
    repositories {
        jcenter()
        maven("http://repository.jetbrains.com/kotlin-nosql") // required for kotlin-nosql
    }
    tasks.withType<Delete> {
        delete(projectDir.resolve("out"))
    }
}

tasks {
    "clean"(Delete::class) {
        delete(buildDir)
    }
    "wrapper"(Wrapper::class) {
        gradleVersion = VERSION_GRADLE
    }
}