plugins {
    kotlin("jvm")
    idea
    hendraanggrian("r")
    hendraanggrian("packr")
    shadow
    application
}

group = RELEASE_GROUP
version = RELEASE_VERSION

application.mainClassName = "$RELEASE_GROUP.App"

sourceSets {
    getByName("main") {
        // manual import client generated build
        val dirs = mutableListOf("src")
        val clientGeneratedDir = "$RELEASE_ARTIFACT-client/build/generated"
        if (rootDir.resolve(clientGeneratedDir).exists()) {
            dirs += "../$clientGeneratedDir/buildconfig/src/main"
            dirs += "../$clientGeneratedDir/r/src/main"
        }
        java.srcDirs(*dirs.toTypedArray())
        resources.srcDir("res")
    }
    getByName("test") {
        java.srcDir("tests/src")
        resources.srcDir("tests/res")
    }
}

ktlint()

dependencies {
    implementation(project(":$RELEASE_ARTIFACT-client"))
    implementation(kotlinx("coroutines-javafx", VERSION_COROUTINES))

    implementation(slf4j("log4j12"))

    implementation(hendraanggrian("ktfx", "ktfx", VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-controlsfx", VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-jfoenix", VERSION_KTFX))

    implementation(apache("commons-math3", VERSION_COMMONS_MATH))
    implementation(apache("poi-ooxml", VERSION_POI))
    implementation("commons-validator:commons-validator:$VERSION_COMMONS_VALIDATOR")

    implementation(google("guava", "guava", "$VERSION_GUAVA-jre"))

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
    testImplementation(testFx("junit"))
}

tasks {
    named<com.hendraanggrian.r.RTask>("generateR") {
        resourcesDirectory = "res"
        // exclude("font", "license")
        useCss {
            isJavaFx = true
        }
        useProperties()
    }

    named<Jar>("jar") {
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }
    }

    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        destinationDirectory.set(buildDir.resolve("releases"))
        archiveBaseName.set(RELEASE_ARTIFACT)
        archiveVersion.set(RELEASE_VERSION)
        archiveClassifier.set(null as String?)
    }

    withType<com.hendraanggrian.packr.PackTask> {
        dependsOn("installDist")
    }
}

packr {
    mainClass = application.mainClassName
    executable = RELEASE_NAME
    classpath = files("build/install/$RELEASE_ARTIFACT-client-javafx/lib")
    resources = files("res")
    vmArgs("Xmx2G")
    macOS {
        name = "$RELEASE_NAME/$RELEASE_NAME.app"
        icon = rootProject.projectDir.resolve("art/$RELEASE_NAME.icns")
        bundleId = RELEASE_GROUP
    }
    windows32 {
        name = "32-bit/$RELEASE_NAME"
        jdk = "/Volumes/Media/Windows JDK/jdk1.8.0_231-x86"
    }
    windows64 {
        name = "64-bit/$RELEASE_NAME"
        jdk = "/Volumes/Media/Windows JDK/jdk1.8.0_231-x64"
    }
    verbose = true
    openOnDone = true
}