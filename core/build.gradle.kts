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
    api(kotlinx("coroutines-core", VERSION_COROUTINES))
    api(mongodb())
    api(jodaTime())

    api(square("converter-gson", VERSION_RETROFIT, "retrofit2"))
    api(jakeWharton("retrofit", "retrofit2-kotlin-coroutines-experimental-adapter", "1.0.0"))

    api(google("gson", VERSION_GSON, "code.gson"))
    api(google("guava", VERSION_GUAVA, "guava"))

    testImplementation(junit())
    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
}

tasks {
    "dokka"(org.jetbrains.dokka.gradle.DokkaTask::class) {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }
}