plugins {
    kotlin("jvm")
    kotlin("spring")
    dokka()
    `spring-boot`
    `dependency-management`
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

    implementation(springBoot("starter-web"))

    testImplementation(junit())
    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
    testImplementation(springBoot("starter-test"))
}

tasks {
    "dokka"(org.jetbrains.dokka.gradle.DokkaTask::class) {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }
}