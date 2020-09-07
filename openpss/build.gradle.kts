import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hendraanggrian.buildconfig.BuildConfigTask
import com.hendraanggrian.r.RTask

plugins {
    kotlin("jvm")
    idea
    id("com.hendraanggrian.r")
    id("com.hendraanggrian.buildconfig")
    id("com.hendraanggrian.packr")
    shadow
    application
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

ktlint()

dependencies {
    implementation(kotlin("stdlib", VERSION_KOTLIN))
    implementation(kotlin("nosql-mongodb", VERSION_NOSQL))
    implementation(kotlinx("coroutines-javafx", VERSION_COROUTINES))

    implementation(square("adapter-guava", VERSION_RETROFIT, "retrofit2"))
    implementation(square("converter-gson", VERSION_RETROFIT, "retrofit2"))

    implementation(google("gson", VERSION_GSON, "code.gson"))
    implementation(google("guava", VERSION_GUAVA, "guava"))

    implementation(hendraanggrian("ktfx", "ktfx", VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-controlsfx", VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-jfoenix", VERSION_KTFX))

    implementation(jodaTime())

    implementation(apache("maven-artifact", VERSION_MAVEN))
    implementation(apache("commons-lang3", VERSION_COMMONS_LANG))
    implementation(apache("commons-math3", VERSION_COMMONS_MATH))
    implementation(apache("poi-ooxml", VERSION_POI))
    implementation(commonsValidator())

    implementation(log4j12())

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
    testImplementation(testFx("junit"))
}

packr {
    executable = RELEASE_NAME
    mainClass = application.mainClassName
    classpath = files("build/install/$RELEASE_ARTIFACT/lib")
    resources = files("res")
    vmArgs("Xmx2G")
    macOS {
        name = "$RELEASE_NAME/$RELEASE_NAME.app"
        icon = rootProject.projectDir.resolve("art/$RELEASE_NAME.icns")
        bundleId = RELEASE_GROUP
    }
    windows32 {
        name = "32-bit/$RELEASE_NAME"
        jdk = "/Volumes/Media/Windows JDK/jdk1.8.0_261-x86"
    }
    windows64 {
        name = "64-bit/$RELEASE_NAME"
        jdk = "/Volumes/Media/Windows JDK/jdk1.8.0_261-x64"
    }
    isVerbose = true
    isAutoOpen = true
}

tasks {
    "generateR"(RTask::class) {
        resourcesDir = projectDir.resolve("res")
        exclude("font", "license")
        configureCss()
        configureProperties {
            isWriteResourceBundle = true
        }
    }
    "generateBuildConfig"(BuildConfigTask::class) {
        appName = RELEASE_NAME
        debug = RELEASE_DEBUG
        artifactId = RELEASE_ARTIFACT
        email = "$RELEASE_USER@gmail.com"
        website = RELEASE_WEBSITE

        addField("AUTHOR", RELEASE_USER)
        addField("FULL_NAME", RELEASE_FULL_NAME)
    }

    "shadowJar"(ShadowJar::class) {
        destinationDirectory.set(buildDir.resolve("release"))
        archiveBaseName.set(RELEASE_ARTIFACT)
        archiveVersion.set(RELEASE_VERSION)
        archiveClassifier.set(null as String?)
    }

    withType<com.hendraanggrian.packr.PackTask> {
        dependsOn("installDist")
    }
}