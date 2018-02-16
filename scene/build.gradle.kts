import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.kotlin

group = "$releaseGroup.scene"
version = releaseVersion

plugins {
    `java-library`
    kotlin("jvm")
    idea
    r
    shadow
}

java.sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("sceneres")
    }
}

r.resourcesDir = "sceneres"

configurations.create("ktlint")

dependencies {
    compile(kotlin("stdlib", kotlinVersion))
    compile(hendraanggrian("kotfx", kotfxVersion))
    compile(jodaTime())
    compile(commonsValidator())
    ktlint()
}

(tasks["shadowJar"] as ShadowJar).apply {
    destinationDir = project.file("../release")
    baseName = "$releaseArtifact-scene"
    classifier = null
}

task<JavaExec>("ktlint") {
    group = "verification"
    inputs.dir("src")
    outputs.dir("src")
    description = "Check Kotlin code style."
    classpath = configurations["ktlint"]
    main = "com.github.shyiko.ktlint.Main"
    args("src/**/*.kt")
}
tasks["check"].dependsOn(tasks["ktlint"])
task<JavaExec>("ktlintFormat") {
    group = "formatting"
    inputs.dir("src")
    outputs.dir("src")
    description = "Fix Kotlin code style deviations."
    classpath = configurations["ktlint"]
    main = "com.github.shyiko.ktlint.Main"
    args("-F", "src/**/*.kt")
}