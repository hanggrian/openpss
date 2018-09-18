import org.gradle.api.JavaVersion.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    register("clean", Delete::class) {
        delete(buildDir)
    }
    register("wrapper", Wrapper::class) {
        gradleVersion = VERSION_GRADLE
    }
}