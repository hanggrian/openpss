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
    implementation(kotlin("stdlib", VERSION_KOTLIN))
    implementation(kotlin("nosql-mongodb", VERSION_NOSQL))
    implementation(kotlinx("coroutines-core", VERSION_COROUTINES))

    implementation(jodaTime())
    implementation(jodaTimeSerializers())
    implementation(google("code.gson", "gson", VERSION_GSON))
    implementation(apache("maven-artifact", VERSION_MAVEN))

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
}
