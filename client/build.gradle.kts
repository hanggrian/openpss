plugins {
    `java-library`
    kotlin("jvm")
    dokka()
    idea
    generating("buildconfig")
    generating("r")
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

    api(ktor("client-okhttp"))
    api(ktor("client-gson"))

    api(androidx("annotation", version = "1.0.1"))

    testImplementation(junit())
}

tasks {
    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }

    named<com.hendraanggrian.generating.buildconfig.BuildConfigTask>("generateBuildConfig") {
        className = "BuildConfig2"
        appName = RELEASE_NAME
        debug = RELEASE_DEBUG
        artifactId = RELEASE_ARTIFACT
        email = "$RELEASE_USER@gmail.com"
        website = RELEASE_WEBSITE
        field("USER", RELEASE_USER)
        field("FULL_NAME", RELEASE_FULL_NAME)
    }

    named<com.hendraanggrian.generating.r.RTask>("generateR") {
        className = "R2"
        resourcesDirectory = projectDir.resolve("res")
        configureProperties {
            readResourceBundle = true
        }
    }
}