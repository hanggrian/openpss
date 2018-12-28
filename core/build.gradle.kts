plugins {
    `java-library`
    kotlin("jvm")
    dokka()
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
    api(kotlin("stdlib", VERSION_KOTLIN))
    api(kotlin("nosql-mongodb", VERSION_NOSQL))
    api(kotlinx("coroutines-core", VERSION_COROUTINES)) // non-platform specific coroutines

    api(jodaTime())
    api(google("gson", VERSION_GSON, "code.gson"))
    api(jodaTimeGsonSerializers())
    api(apache("maven-artifact", VERSION_MAVEN))

    testImplementation(junit())
    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
}

tasks {
    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }
}