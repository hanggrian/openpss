buildscript {
    repositories {
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots") // required for packr
        maven("https://dl.bintray.com/hendraanggrian/packr")
        maven("https://dl.bintray.com/hendraanggrian/generation")
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
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions.jvmTarget = "${JavaVersion.VERSION_1_8}"
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