buildscript {
    repositories {
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://dl.bintray.com/hendraanggrian/packr")
    }
    dependencies {
        classpath(kotlin("gradle-plugin", VERSION_KOTLIN))
        classpath(dokka())
        classpath(hendraanggrian("r-gradle-plugin", VERSION_R))
        classpath(hendraanggrian("buildconfig-gradle-plugin", VERSION_BUILDCONFIG))
        classpath(hendraanggrian("packr-gradle-plugin", VERSION_PACKR))
        classpath(shadow())
        classpath(gitPublish())
        classpath("com.helger:ph-css:6.1.1")
    }
}

allprojects {
    repositories {
        jcenter()
        maven("http://repository.jetbrains.com/kotlin-nosql")
    }
    tasks {
        withType<Delete> {
            delete(projectDir.resolve("out"))
        }
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(buildDir)
}