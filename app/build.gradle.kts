import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hendraanggrian.buildconfig.BuildConfigTask
import com.hendraanggrian.packr.PackTask
import com.hendraanggrian.r.RTask
import org.gradle.api.plugins.ExtensionAware
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.Coroutines.*
import org.gradle.language.base.plugins.LifecycleBasePlugin.*

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
    application
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

application.mainClassName = "$group.App"

kotlin.experimental.coroutines = ENABLE

val ktlint by configurations.creating

dependencies {
    implementation(project(":scene"))
    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotlin("nosql-mongodb", nosqlVersion))
    implementation(hendraanggrian("ktfx", ktfxVersion, "ktfx"))
    implementation(apache("commons-lang3", commonsLangVersion))
    implementation(apache("poi-ooxml", poiVersion))
    implementation(guava())
    implementation(log4j12())

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

    ktlint(ktlint())
}

tasks {
    withType<RTask> {
        resourcesDir = projectDir.resolve("res")
        lowercase = true
    }
    withType<BuildConfigTask> {
        appName = releaseName
        debug = releaseDebug
        field("ARTIFACT", releaseArtifact)
        field("WEBSITE", releaseWebsite)
    }

    "ktlint"(JavaExec::class) {
        get("check").dependsOn(this)
        group = VERIFICATION_GROUP
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

    withType<ShadowJar> {
        destinationDir = buildDir.resolve("release")
        manifest.attributes(mapOf("Main-Class" to application.mainClassName))
        baseName = releaseArtifact
        version = releaseVersion
        classifier = null
    }
    withType<PackTask> {
        dependsOn("installDist")

        classpath(*buildDir.resolve("install/app/lib").listFiles() ?: emptyArray())
        executable = releaseName
        mainClass = application.mainClassName
        vmArgs("Xmx2G")
        resources("res", "../scene/sceneres")
        outputName = releaseName

        iconDir = rootProject.projectDir.resolve("art").resolve("$releaseName.icns")
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