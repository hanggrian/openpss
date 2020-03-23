plugins {
    `java-library`
    kotlin("jvm")
    idea
    hendraanggrian("r")
    hendraanggrian("buildconfig")
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
    api(project(":$RELEASE_ARTIFACT"))

    api(hendraanggrian("prefs", "prefs", VERSION_PREFS))
    api(ktor("client-okhttp"))
    api(ktor("client-gson"))
    api(arrow("core"))

    api(androidx("annotation", version = "1.0.1"))

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
}

tasks {
    named<com.hendraanggrian.buildconfig.BuildConfigTask>("generateBuildConfig") {
        className = "BuildConfig2"
        appName = RELEASE_NAME
        debug = RELEASE_DEBUG
        artifactId = RELEASE_ARTIFACT
        email = "$RELEASE_USER@gmail.com"
        website = RELEASE_WEBSITE
        addField("USER", RELEASE_USER)
        addField("FULL_NAME", RELEASE_FULL_NAME)
    }

    named<com.hendraanggrian.r.RTask>("generateR") {
        className = "R2"
        resourcesDirectory = "res"
        properties {
            isWriteResourceBundle = true
        }
    }
}