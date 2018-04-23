import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hendraanggrian.buildconfig.BuildConfigTask
import com.hendraanggrian.packr.PackTask
import com.hendraanggrian.r.RTask
import org.gradle.api.JavaVersion.VERSION_1_10
import org.gradle.api.plugins.ExtensionAware
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.Coroutines.*
import org.gradle.language.base.plugins.LifecycleBasePlugin.*

import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

group = "$RELEASE_GROUP.$RELEASE_ARTIFACT"
version = RELEASE_VERSION

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

java {
    sourceSets {
        "main" {
            java.srcDir("src")
            resources.srcDir("res")
        }
        "test" {
            java.srcDir("tests/src")
        }
    }
}

application.mainClassName = "$group.App"

kotlin.experimental.coroutines = ENABLE

val ktlint by configurations.creating

dependencies {
    implementation(project(":scene"))
    implementation(kotlin("stdlib", VERSION_KOTLIN))
    implementation(kotlin("nosql-mongodb", VERSION_NOSQL))
    implementation(square("retrofit", VERSION_RETROFIT, "retrofit2"))
    implementation(square("converter-gson", VERSION_RETROFIT, "retrofit2"))
    implementation(google("gson", VERSION_GSON, "code.gson"))
    implementation(google("guava", VERSION_GUAVA, "guava"))
    implementation(hendraanggrian("ktfx", VERSION_KTFX, "ktfx"))
    implementation(apache("commons-lang3", VERSION_COMMONS_LANG))
    implementation(apache("poi-ooxml", VERSION_POI))
    implementation(log4j12())

    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
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
        appName = RELEASE_NAME
        debug = RELEASE_DEBUG
        field("USER", RELEASE_USER)
        field("FULL_NAME", RELEASE_FULL_NAME)
        field("ARTIFACT", RELEASE_ARTIFACT)
        field("WEBSITE", RELEASE_WEBSITE)
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
        baseName = RELEASE_ARTIFACT
        version = RELEASE_VERSION
        classifier = null
    }
    withType<PackTask> {
        dependsOn("installDist")

        classpath(*buildDir.resolve("install/app/lib").listFiles() ?: emptyArray())
        executable = RELEASE_NAME
        mainClass = application.mainClassName
        vmArgs("Xmx2G")
        resources("res", "../scene/sceneres")
        outputName = RELEASE_NAME

        iconDir = rootProject.projectDir.resolve("art").resolve("$RELEASE_NAME.icns")
        bundleId = RELEASE_GROUP
        verbose = true
        openOnDone = true
    }
}

configure<JUnitPlatformExtension> {
    if (this is ExtensionAware) extensions.getByType(FiltersExtension::class.java).apply {
        if (this is ExtensionAware) extensions.getByType(EnginesExtension::class.java).include("spek")
    }
}