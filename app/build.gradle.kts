import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.plugins.ExtensionAware
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.Coroutines.*

import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

group = releaseGroup
version = releaseVersion

plugins {
    java
    kotlin("jvm")
    idea
    r
    buildconfig
    shadow
    packr
    `junit-platform`
}

java.sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("res")
    }
    get("test").java.srcDir("tests/src")
}

kotlin.experimental.coroutines = ENABLE

r.resourcesDirectory = "res"

buildconfig {
    name = releaseArtifact
    debug = releaseDebug
}

val ktlint by configurations.creating

dependencies {
    implementation(project(":scene"))
    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotlin("nosql-mongodb", nosqlVersion))
    implementation(kotlinx("coroutines-javafx", coroutinesVersion))
    implementation(apache("commons-lang3", commonsLangVersion))
    implementation(apache("poi-ooxml", poiVersion))
    implementation(guava())
    implementation(log4j12())
    ktlint(ktlint())
    testImplementation(kotlin("test", kotlinVersion))
    testImplementation(kotlin("reflect", kotlinVersion))
    testImplementation(spek("api", spekVersion)) {
        exclude("org.jetbrains.kotlin")
    }
    testRuntime(spek("junit-platform-engine", spekVersion)) {
        exclude("org.jetbrains.kotlin")
        exclude("org.junit.platform")
    }
    testImplementation(junitPlatform("runner", junitPlatformVersion))
}

tasks {
    val ktlint by creating(JavaExec::class) {
        group = "verification"
        inputs.dir("src")
        outputs.dir("src")
        description = "Check Kotlin code style."
        classpath = configurations["ktlint"]
        main = "com.github.shyiko.ktlint.Main"
        args("src/**/*.kt")
    }
    get("check").dependsOn(ktlint)
    "ktlintFormat"(JavaExec::class) {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath = configurations["ktlint"]
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "src/**/*.kt")
    }

    val shadowJar by getting(ShadowJar::class) {
        destinationDir = buildDir.resolve("release")
        manifest.attributes(mapOf("Main-Class" to "$releaseGroup.App"))
        baseName = releaseArtifact
        version = releaseVersion
        classifier = null
    }


    packr {
        platforms.mac = "/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home"
        platforms.windows64 = "C:/Program Files/Java/jdk1.8.0_162"

        classpath("build/release/$releaseArtifact-$releaseVersion.jar")
        executable = releaseArtifact
        mainClass = "$releaseGroup.App"
        vmArgs("Xmx2G")
        resources("res", "../scene/sceneres")
        outputName = "Wijaya Printing"

        icon = "mac.icns"
        bundle = releaseGroup
    }
}

configure<JUnitPlatformExtension> {
    if (this is ExtensionAware) extensions.getByType(FiltersExtension::class.java).apply {
        if (this is ExtensionAware) extensions.getByType(EnginesExtension::class.java).include("spek")
    }
}