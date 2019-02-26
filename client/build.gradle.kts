plugins {
    `java-library`
    kotlin("jvm")
    dokka()
    idea
    id("com.hendraanggrian.r")
    id("com.hendraanggrian.buildconfig")
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

ktlint()

dependencies {
    api(project(":core"))

    api(hendraanggrian("defaults", "defaults", VERSION_DEFAULTS))
    api(ktor("client-okhttp"))
    api(ktor("client-gson"))

    api(androidx("annotation", version = "1.0.1"))

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
}

tasks {
    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }

    named<com.hendraanggrian.buildconfig.BuildConfigTask>("generateBuildConfig") {
        className = "BuildConfig2"
        appName = RELEASE_NAME
        debug = RELEASE_DEBUG
        artifactId = RELEASE_ARTIFACT
        email = "$RELEASE_USER@gmail.com"
        website = RELEASE_WEBSITE
        field("USER", RELEASE_USER)
        field("FULL_NAME", RELEASE_FULL_NAME)
    }

    named<com.hendraanggrian.r.RTask>("generateR") {
        className = "R2"
        resourcesDirectory = "res"
        useProperties {
            readResourceBundle = true
        }
    }
}