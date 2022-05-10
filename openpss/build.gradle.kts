plugins {
    `java-library`
    kotlin("jvm")
}

group = RELEASE_GROUP
version = RELEASE_VERSION

sourceSets {
    named("main") {
        java.srcDir("src")
        resources.srcDir("res")
    }
    named("test") {
        java.srcDir("tests/src")
        resources.srcDir("tests/res")
    }
}

ktlint()

dependencies {
    api(kotlin("stdlib", VERSION_KOTLIN))
    api(kotlin("nosql-mongodb", VERSION_NOSQL))
    api(kotlinx("coroutines-core", VERSION_COROUTINES))

    api(jodaTime())
    api(jodaTimeSerializers())
    api(google("code.gson", "gson", VERSION_GSON))
    api(apache("maven-artifact", VERSION_MAVEN))

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
}
