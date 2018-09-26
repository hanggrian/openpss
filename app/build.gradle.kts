import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hendraanggrian.generation.buildconfig.BuildConfigTask
import com.hendraanggrian.generation.r.RTask
import com.hendraanggrian.packr.PackTask
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.Coroutines

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

kotlin.experimental.coroutines = Coroutines.ENABLE

val ktlint by configurations.registering

dependencies {
    implementation(project(":scene"))
    implementation(kotlin("stdlib", VERSION_KOTLIN))
    implementation(kotlin("nosql-mongodb", VERSION_NOSQL))
    implementation(square("adapter-guava", VERSION_RETROFIT, "retrofit2"))
    implementation(square("converter-gson", VERSION_RETROFIT, "retrofit2"))
    implementation(google("gson", VERSION_GSON, "code.gson"))
    implementation(google("guava", VERSION_GUAVA, "guava"))
    implementation(hendraanggrian("ktfx", version = VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-controlsfx", VERSION_KTFX))
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

    ktlint {
        invoke(ktlint())
    }
}

tasks {
    "generateR"(RTask::class) {
        resourcesDirectory = projectDir.resolve("res")
        setLowercase(true)
    }
    "generateBuildConfig"(BuildConfigTask::class) {
        appName = RELEASE_NAME
        debug = RELEASE_DEBUG
        artifactId = RELEASE_ARTIFACT
        author = RELEASE_USER
        email = "$RELEASE_USER@gmail.com"
        website = RELEASE_WEBSITE
        field("FULL_NAME", RELEASE_FULL_NAME)
    }

    val ktlint by registering(JavaExec::class) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        inputs.dir("src")
        outputs.dir("src")
        description = "Check Kotlin code style."
        classpath(configurations["ktlint"])
        main = "com.github.shyiko.ktlint.Main"
        args("src/**/*.kt")
    }
    "check" {
        dependsOn(ktlint)
    }
    register("ktlintFormat", JavaExec::class) {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath(configurations["ktlint"])
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "src/**/*.kt")
    }

    "shadowJar"(ShadowJar::class) {
        destinationDir = buildDir.resolve("release")
        manifest.attributes(mapOf("Main-Class" to application.mainClassName))
        baseName = RELEASE_ARTIFACT
        version = RELEASE_VERSION
        classifier = null
    }

    "pack"(PackTask::class) {
        dependsOn("installDist")

        buildDir.resolve("install/app/lib")?.listFiles()?.forEach {
            classpath.add(it.path)
        }
        executable = RELEASE_NAME
        mainClass = application.mainClassName
        vmArgs.add("Xmx2G")
        resources.addAll(listOf("res", "../scene/sceneres"))
        mac {
            name = "$RELEASE_NAME.app"
            icon = "art/$RELEASE_NAME.icns"
            bundleId = RELEASE_GROUP
        }
        windows64 {
            jdk = "/Users/hendraanggrian/Desktop/jdk1.8.0_172"
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