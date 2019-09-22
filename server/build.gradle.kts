plugins {
    kotlin("jvm")
    dokka()
    idea
    id("com.hendraanggrian.r")
    id("com.hendraanggrian.buildconfig")
    shadow
    application
}

group = RELEASE_GROUP
version = RELEASE_VERSION

application.mainClassName = "$group.OpenPSSServer"

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

    implementation(ktor("server-netty"))
    implementation(ktor("websockets"))
    implementation(ktor("gson"))

    implementation(logback("classic"))
    implementation(mongo("java-driver"))

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
    testImplementation(slf4j("log4j12"))
}

tasks {
    named<com.hendraanggrian.r.RTask>("generateR") {
        resourcesDirectory = "res"
        useProperties {
            readResourceBundle = true
        }
    }

    named<com.hendraanggrian.buildconfig.BuildConfigTask>("generateBuildConfig") {
        appName = "$RELEASE_NAME Server"
        debug = RELEASE_DEBUG
        website = RELEASE_WEBSITE
        field("DATABASE_NAME", RELEASE_ARTIFACT)
        field("DATABASE_USER", DATABASE_USER)
        field("DATABASE_PASS", DATABASE_PASS)
    }


    named<Jar>("jar") {
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }
    }

    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        destinationDir = buildDir.resolve("releases")
        baseName = "$RELEASE_ARTIFACT-server"
        version = RELEASE_VERSION
        classifier = null
    }

    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }
}
