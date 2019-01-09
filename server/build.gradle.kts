plugins {
    kotlin("jvm")
    dokka()
    idea
    generating("r")
    generating("buildconfig")
    shadow
    application
}

group = RELEASE_GROUP
version = RELEASE_VERSION

application.mainClassName = "$group.OpenPssServer"

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

    implementation("ch.qos.logback:logback-classic:$VERSION_LOGBACK")
    implementation("org.mongodb:mongo-java-driver:$VERSION_MONGODB")

    testImplementation(junit())
    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
    testImplementation(slf4j("log4j12"))
}

tasks {
    named<com.hendraanggrian.generating.r.RTask>("generateR") {
        resourcesDirectory = projectDir.resolve("res")
        configureProperties {
            readResourceBundle = true
        }
    }

    named<com.hendraanggrian.generating.buildconfig.BuildConfigTask>("generateBuildConfig") {
        appName = "$RELEASE_NAME Server"
        debug = RELEASE_DEBUG
        website = RELEASE_WEBSITE
        field("DATABASE_NAME", RELEASE_ARTIFACT)
        field("DATABASE_USER", env(DATABASE_USER))
        field("DATABASE_PASS", env(DATABASE_PASS))
        field("SERVER_HOST", env(SERVER_HOST))
        field("SERVER_PORT", env(SERVER_PORT).toInt())
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