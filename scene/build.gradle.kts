import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.kotlin

group = releaseGroup
version = releaseVersion

plugins {
    `java-library`
    kotlin("jvm")
    shadow
}

java.sourceSets.getByName("main").java.srcDir("src")

dependencies {
    compile(kotlin("stdlib", kotlinVersion))
    compile(hendraanggrian("kotfx", kotfxVersion))
    compile(jodaTime())
    compile(commonsValidator())
}

(tasks["shadowJar"] as ShadowJar).apply {
    destinationDir = project.file("../release")
    baseName = "$releaseArtifact-scene"
    classifier = null
}