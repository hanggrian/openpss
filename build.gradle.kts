import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.kotlin
import java.io.File
import java.nio.file.Files.delete

buildscript {
    repositories {
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath(hendraanggrian(null, "r", rVersion))
        classpath(hendraanggrian(null, "buildconfig", buildconfigVersion))
        classpath(hendraanggrian(null, "packr", packrVersion))
        classpath(shadow())
        classpath(junitPlatform("gradle-plugin"))
    }
}

allprojects {
    repositories {
        jcenter()
        maven("http://repository.jetbrains.com/kotlin-nosql")
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
        gradleVersion = "4.5.1"
    }
}