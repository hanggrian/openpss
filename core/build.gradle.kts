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

    api("joda-time:joda-time:$VERSION_JODA_TIME")
    api("com.fatboyindustrial.gson-jodatime-serialisers:gson-jodatime-serialisers:$VERSION_JODA_TIME_GSON_SERIALIZERS")
    api(google("code.gson", "gson", VERSION_GSON))
    api(apache("maven-artifact", VERSION_MAVEN))

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
}

tasks {
    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }
}