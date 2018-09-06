import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hendraanggrian.generation.buildconfig.BuildConfigTask
import com.hendraanggrian.generation.r.RTask
import com.hendraanggrian.packr.PackTask
import org.codehaus.groovy.ast.tools.GeneralUtils.args
import org.gradle.api.plugins.ExtensionAware
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.Coroutines.*
import org.gradle.language.base.plugins.LifecycleBasePlugin.*
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

group = RELEASE_GROUP
version = RELEASE_VERSION

plugins {
    java
    kotlin("jvm")
    idea
    generation("r")
    generation("buildconfig")
    shadow
    application
    packr
    `junit-platform`
}

sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("res")
    }
    getByName("test") {
        java.srcDir("tests/src")
    }
}

application.mainClassName = "$group.App"

kotlin.experimental.coroutines = ENABLE

val ktlint by configurations.registering

dependencies {
    implementation(project(":scene"))
    implementation(kotlin("stdlib", VERSION_KOTLIN))
    implementation(kotlin("nosql-mongodb", VERSION_NOSQL))
    implementation(square("adapter-guava", VERSION_RETROFIT, "retrofit2"))
    implementation(square("converter-gson", VERSION_RETROFIT, "retrofit2"))
    implementation(google("gson", VERSION_GSON, "code.gson"))
    implementation(google("guava", VERSION_GUAVA, "guava"))
    implementation(hendraanggrian("javafxx", version = VERSION_JAVAFXX))
    implementation(hendraanggrian("javafxx", "layouts-controlsfx-ktx", VERSION_JAVAFXX))
    implementation(apache("maven-artifact", VERSION_MAVEN))
    implementation(apache("commons-lang3", VERSION_COMMONS_LANG))
    implementation(apache("commons-math3", VERSION_COMMONS_MATH))
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

    ktlint(this, ktlint())
}

tasks {
    withType<RTask> {
        resourcesDir = projectDir.resolve("res")
        lowercase = true
    }
    withType<BuildConfigTask> {
        appName = RELEASE_NAME
        debug = RELEASE_DEBUG
        field(String::class.java, "USER", RELEASE_USER)
        field(String::class.java, "FULL_NAME", RELEASE_FULL_NAME)
        field(String::class.java, "ARTIFACT", RELEASE_ARTIFACT)
        field(String::class.java, "WEBSITE", RELEASE_WEBSITE)
    }

    register("ktlint", JavaExec::class) {
        get("check").dependsOn(ktlint)
        group = VERIFICATION_GROUP
        inputs.dir("src")
        outputs.dir("src")
        description = "Check Kotlin code style."
        classpath(ktlint())
        main = "com.github.shyiko.ktlint.Main"
        args("src/**.kt")
    }
    register("ktlintformat", JavaExec::class) {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath(ktlint())
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "src*.kt")
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

        buildDir.resolve("install/app/lib")?.listFiles()?.forEach {
            classpath.add(it.path)
        }
        executable = RELEASE_NAME
        mainClass = application.mainClassName
        vmArgs.add("Xmx2G")
        resources.addAll(listOf("res", "../scene/sceneres"))
        mac("/Library/Java/JavaVirtualMachines/jdk1.8.0_172.jdk/Contents/Home") {
            name = "$RELEASE_NAME.app"
            icon = "art/$RELEASE_NAME.icns"
            bundleId = RELEASE_GROUP
        }
        windows64("/Users/hendraanggrian/Desktop/jdk1.8.0_172") {
            name = RELEASE_NAME
        }
        verbose = true
        openOnDone = true
    }
}

configure<JUnitPlatformExtension> {
    if (this is ExtensionAware) extensions.getByType(FiltersExtension::class.java).apply {
        if (this is ExtensionAware) extensions.getByType(EnginesExtension::class.java).include("spek")
    }
}