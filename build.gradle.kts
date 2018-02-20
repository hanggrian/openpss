import org.gradle.kotlin.dsl.kotlin

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
        maven("https://dl.bintray.com/hendraanggrian/maven") // temporary until kotfx libraries are approved
        maven("http://repository.jetbrains.com/kotlin-nosql")
    }
    tasks.withType(Delete::class.java) {
        delete(File(projectDir, "out"))
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

task<Wrapper>("wrapper"){
    gradleVersion = "4.5"
}