import org.gradle.api.plugins.ExtensionAware
import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

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

group = RELEASE_GROUP
version = RELEASE_VERSION

sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("res")
    }
    getByName("test") {
        java.srcDir("tests/src")
        resources.srcDir("tests/res")
    }
}

application.mainClassName = "$group.App"

kotlin.experimental.coroutines = org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE

ktlint()

dependencies {
    implementation(kotlin("stdlib", VERSION_KOTLIN))
    implementation(kotlin("nosql-mongodb", VERSION_NOSQL))
    implementation(kotlinx("coroutines-javafx", VERSION_COROUTINES))

    implementation(square("adapter-guava", VERSION_RETROFIT, "retrofit2"))
    implementation(square("converter-gson", VERSION_RETROFIT, "retrofit2"))

    implementation(google("gson", VERSION_GSON, "code.gson"))
    implementation(google("guava", VERSION_GUAVA, "guava"))

    implementation(hendraanggrian("ktfx", version = VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-controlsfx", VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-jfoenix", VERSION_KTFX))

    implementation(jodaTime())

    implementation(apache("maven-artifact", VERSION_MAVEN))
    implementation(apache("commons-lang3", VERSION_COMMONS_LANG))
    implementation(apache("commons-math3", VERSION_COMMONS_MATH))
    implementation(apache("poi-ooxml", VERSION_POI))
    implementation(commonsValidator())

    implementation(log4j12())

    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))

    testImplementation(testFx("core"))
    testImplementation(testFx("junit"))

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
    "generateR"(com.hendraanggrian.generation.r.RTask::class) {
        resourcesDir = projectDir.resolve("res")
        isLowercase = true
    }

    "generateBuildConfig"(com.hendraanggrian.generation.buildconfig.BuildConfigTask::class) {
        appName = RELEASE_NAME
        debug = RELEASE_DEBUG
        artifactId = RELEASE_ARTIFACT
        author = RELEASE_USER
        email = "$RELEASE_USER@gmail.com"
        website = RELEASE_WEBSITE
        field("FULL_NAME", RELEASE_FULL_NAME)
    }

    "shadowJar"(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
        destinationDir = buildDir.resolve("release")
        manifest.attributes(mapOf("Main-Class" to application.mainClassName))
        baseName = RELEASE_ARTIFACT
        version = RELEASE_VERSION
        classifier = null
    }

    "pack"(com.hendraanggrian.packr.PackTask::class) {
        dependsOn("installDist")

        buildDir.resolve("install/$RELEASE_ARTIFACT/lib")?.listFiles()?.forEach { classpath(it.path) }
        executable = RELEASE_NAME
        mainClass = application.mainClassName
        vmArgs("Xmx2G")
        resources(projectDir.resolve("res"))
        macOS {
            name = "$RELEASE_NAME.app"
            icon = projectDir.resolve("art/$RELEASE_NAME.icns")
            bundleId = RELEASE_GROUP
        }
        windows64 {
            jdk = "/Users/hendraanggrian/Desktop/jdk1.8.0_181"
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