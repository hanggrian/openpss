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

    api(kotlin("stdlib", VERSION_KOTLIN))

    api(apache("commons-lang3", VERSION_COMMONS_LANG))

    testImplementation(junit())
    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
}

tasks {
    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }

    named<com.hendraanggrian.generating.buildconfig.BuildConfigTask>("generateBuildConfig") {
        packageName = "$RELEASE_GROUP.internal"
        className = "CommonJvmBuildConfig"
        artifactId = RELEASE_ARTIFACT
    }
}