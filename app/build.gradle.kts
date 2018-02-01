import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.Coroutines.*

import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

plugins {
    java
    kotlin("jvm")
    r
    buildconfig
    `junit-platform`
}

java.sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("res")
    }
    getByName("test").java.srcDir("tests/src")
}

r {
    srcDir("src")
    resDir("res")
    packageName(releaseGroup)
    leadingSlash(true)
}

buildconfig {
    srcDir("src")
    packageName(releaseGroup)
    artifactId(releaseArtifact)
    version(releaseVersion)
    debug(releaseDebug)
}

configure<JUnitPlatformExtension> {
    filters {
        engines {
            include("spek")
        }
    }
}

kotlin {
    experimental.coroutines = ENABLE
}

dependencies {
    implementation(project(":scene"))

    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotlin("nosql-mongodb", nosqlVersion))
    implementation(kotlinx("coroutines-jdk8", coroutinesVersion))
    implementation(kotlinx("coroutines-javafx", coroutinesVersion))

    implementation(apache("commons-lang3", commonsLangVersion))
    implementation(apache("poi-ooxml", poiVersion))

    implementation(guava())
    implementation(log4j12())

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

fun JUnitPlatformExtension.filters(setup: FiltersExtension.() -> Unit) = when (this) {
    is ExtensionAware -> extensions.getByType(FiltersExtension::class.java).setup()
    else -> error("${this::class} must be an instance of ExtensionAware")
}

fun FiltersExtension.engines(setup: EnginesExtension.() -> Unit) = when (this) {
    is ExtensionAware -> extensions.getByType(EnginesExtension::class.java).setup()
    else -> error("${this::class} must be an instance of ExtensionAware")
}