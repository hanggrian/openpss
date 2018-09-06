import org.gradle.api.JavaVersion.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.kotlin
import java.io.File
import java.nio.file.Files.delete

buildscript {
    repositories {
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots") // required for packr
        maven("https://dl.bintray.com/hendraanggrian/packr") // packr is not approved by jcenter yet
    }
    dependencies {
        classpath(kotlin("gradle-plugin", VERSION_KOTLIN))
        classpath(hendraanggrian("generation", "r-gradle-plugin", VERSION_R))
        classpath(hendraanggrian("generation", "buildconfig-gradle-plugin", VERSION_BUILDCONFIG))
        classpath(hendraanggrian("packr", "packr-gradle-plugin", VERSION_PACKR))
        classpath(shadow())
        classpath(junitPlatform("gradle-plugin"))
    }
}

allprojects {
    repositories {
        jcenter()
        maven("http://repository.jetbrains.com/kotlin-nosql") // required for kotlin-nosql
    }
    tasks {
        withType<Delete> {
            delete(projectDir.resolve("out"))
        }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "$VERSION_1_8"
        }
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
    val wrapper by registering(Wrapper::class) {
        gradleVersion = VERSION_GRADLE
    }
}