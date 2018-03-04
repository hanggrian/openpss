import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hendraanggrian.buildconfig.BuildConfigTask
import com.hendraanggrian.packr.PackTask
import com.hendraanggrian.r.RTask
import org.gradle.api.plugins.ExtensionAware
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.Coroutines.*

import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

group = "$releaseGroup.$releaseArtifact"
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
    "main" {
        java.srcDir("src")
        resources.srcDir("res")
    }
    "test" {
        java.srcDir("tests/src")
    }
}

kotlin.experimental.coroutines = ENABLE

val ktlint by configurations.creating

dependencies {
    implementation(project(":scene"))
    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotlin("nosql-mongodb", nosqlVersion))
    implementation(apache("commons-lang3", commonsLangVersion))
    implementation(apache("poi-ooxml", poiVersion))
    implementation(guava())
    implementation(log4j12())
    ktlint(ktlint())
    testImplementation(kotlin("test", kotlinVersion))
    testImplementation(kotlin("reflect", kotlinVersion))
    testImplementation(spek("api")) {
        exclude("org.jetbrains.kotlin")
    }
    testRuntime(spek("junit-platform-engine")) {
        exclude("org.jetbrains.kotlin")
        exclude("org.junit.platform")
    }
    testImplementation(junitPlatform("runner"))
}

tasks {
    withType<RTask> {
        resourcesDir = "res"
        lowercaseField = true
    }
    withType<BuildConfigTask> {
        appName = releaseName
        debug = releaseDebug
        field(String::class.java, "ARTIFACT", releaseArtifact)
    }

    "ktlint"(JavaExec::class) {
        get("check").dependsOn(this)
        group = "verification"
        inputs.dir("src")
        outputs.dir("src")
        description = "Check Kotlin code style."
        classpath = ktlint
        main = "com.github.shyiko.ktlint.Main"
        args("src/**/*.kt")
    }
    "ktlintFormat"(JavaExec::class) {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath = ktlint
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "src/**/*.kt")
    }

    val main = "$releaseGroup.$releaseArtifact.App"
    val shadowJar by getting(ShadowJar::class) {
        destinationDir = buildDir.resolve("release")
        manifest.attributes(mapOf("Main-Class" to main))
        baseName = releaseArtifact
        version = releaseVersion
        classifier = null
    }
    withType<PackTask> {
        dependsOn(shadowJar)
        classpath("build/release/$releaseArtifact-$releaseVersion.jar")
        executable = releaseName
        mainClass = main
        vmArgs("Xmx2G")
        resources("res", "../scene/sceneres")
        outputName = releaseName

        iconDir = rootProject.projectDir.resolve("art").resolve("OpenPSS.icns")
        bundleId = releaseGroup
        verbose = true
        openOnDone = true
    }
}

configure<JUnitPlatformExtension> {
    if (this is ExtensionAware) extensions.getByType(FiltersExtension::class.java).apply {
        if (this is ExtensionAware) extensions.getByType(EnginesExtension::class.java).include("spek")
    }
}