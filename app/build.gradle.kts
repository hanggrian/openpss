import org.gradle.api.plugins.ExtensionAware
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
    resourcesDir = "res"
}

buildconfig {
    name = releaseArtifact
    debug = releaseDebug
}

kotlin {
    experimental.coroutines = ENABLE
}

dependencies {
    implementation(project(":scene"))

    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotlin("nosql-mongodb", nosqlVersion))
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

configure<JUnitPlatformExtension> {
    filters {
        engines {
            include("spek")
        }
    }
}

fun JUnitPlatformExtension.filters(setup: FiltersExtension.() -> Unit) = when (this) {
    is ExtensionAware -> extensions.getByType(FiltersExtension::class.java).setup()
    else -> error("${this::class} must be an instance of ExtensionAware")
}

fun FiltersExtension.engines(setup: EnginesExtension.() -> Unit) = when (this) {
    is ExtensionAware -> extensions.getByType(EnginesExtension::class.java).setup()
    else -> error("${this::class} must be an instance of ExtensionAware")
}