import org.gradle.kotlin.dsl.kotlin
import java.io.File
import java.nio.file.Files.delete

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath(hendraanggrian("r", rVersion))
        classpath(hendraanggrian("buildconfig", buildconfigVersion))
        classpath(shadow())
        classpath(junitPlatform("gradle-plugin", junitPlatformVersion))
    }
}

allprojects {
    repositories {
        jcenter()
        maven("http://repository.jetbrains.com/kotlin-nosql")
    }
    tasks.withType(Delete::class.java) {
        delete(File(projectDir, "out"))
    }
}

tasks {
    "clean"(Delete::class) {
        delete(rootProject.buildDir)
        delete(rootProject.file("release"))
    }
    "wrapper"(Wrapper::class) {
        gradleVersion = "4.5"
    }
}