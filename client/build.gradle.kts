plugins {
    `java-library`
    kotlin("jvm")
    dokka()
    idea
    generating("buildconfig")
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
    api(project(":common"))

    api(ktor("client-okhttp"))
    api(ktor("client-gson"))

    testImplementation(junit())
}

tasks {
    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }

    named<com.hendraanggrian.generating.buildconfig.BuildConfigTask>("generateBuildConfig") {
        packageName = "$RELEASE_GROUP.internal"
        className = "ClientBuildConfig"
        artifactId = RELEASE_ARTIFACT
        field("USER", RELEASE_USER)
    }
}