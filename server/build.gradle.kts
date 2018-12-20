plugins {
    kotlin("jvm")
    id("kotlinx-serialization")
    dokka()
    idea
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
    api(project(":common"))
    api(project(":common-jvm"))

    implementation(mongodb())

    implementation(ktor("server-netty"))
    implementation(ktor("gson"))

    implementation(logback("classic"))

    testImplementation(junit())
    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
    testImplementation(slf4j("log4j12"))
}

tasks {
    named<com.hendraanggrian.generating.r.RTask>("generateR") {
        resourcesDirectory = projectDir.resolve("res")
        properties {
            readResourceBundle = true
        }
    }

    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }
}