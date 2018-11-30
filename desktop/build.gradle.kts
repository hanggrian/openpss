plugins {
    kotlin("jvm")
    dokka()
    idea
    generating("r")
    generating("buildconfig")
    shadow
    application
    packr
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

application.mainClassName = "$group.App"

ktlint()

dependencies {
    api(project(":core"))

    implementation(kotlinx("coroutines-javafx", VERSION_COROUTINES))

    implementation(square("adapter-guava", VERSION_RETROFIT, "retrofit2"))
    implementation(square("converter-gson", VERSION_RETROFIT, "retrofit2"))

    implementation(google("gson", VERSION_GSON, "code.gson"))
    implementation(google("guava", VERSION_GUAVA, "guava"))

    implementation(hendraanggrian("ktfx", version = VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-controlsfx", VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-jfoenix", VERSION_KTFX))

    implementation(apache("maven-artifact", VERSION_MAVEN))
    implementation(apache("commons-lang3", VERSION_COMMONS_LANG))
    implementation(apache("commons-math3", VERSION_COMMONS_MATH))
    implementation(apache("poi-ooxml", VERSION_POI))
    implementation(commonsValidator())

    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))

    testImplementation(hendraanggrian("ktfx", "ktfx-testfx", VERSION_KTFX))
    testImplementation(testFx("junit"))
}

packr {
    mainClass = application.mainClassName
    executable = RELEASE_NAME
    classpath("$buildDir/install/desktop/lib")
    resources("$projectDir/res")
    vmArgs("Xmx2G")
    macOS {
        name = "$RELEASE_NAME.app"
        icon = "${rootProject.projectDir}/art/$RELEASE_NAME.icns"
        bundleId = RELEASE_GROUP
    }
    windows64 {
        jdk = "/Users/hendraanggrian/Desktop/jdk1.8.0_181"
        name = RELEASE_NAME
    }
    verbose = true
    openOnDone = true
}

tasks {
    "dokka"(org.jetbrains.dokka.gradle.DokkaTask::class) {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }

    "generateR"(com.hendraanggrian.generating.r.RTask::class) {
        resourcesDir = projectDir.resolve("res")
        exclude("font", "license")
        css {
            isJavaFx = true
        }
        properties {
            readResourceBundle = true
        }
    }

    "generateBuildConfig"(com.hendraanggrian.generating.buildconfig.BuildConfigTask::class) {
        appName = RELEASE_NAME
        debug = RELEASE_DEBUG
        artifactId = RELEASE_ARTIFACT
        email = "$RELEASE_USER@gmail.com"
        website = RELEASE_WEBSITE

        field("AUTHOR", RELEASE_USER)
        field("FULL_NAME", RELEASE_FULL_NAME)
    }

    "shadowJar"(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
        destinationDir = buildDir.resolve("releases")
        manifest.attributes(mapOf("Main-Class" to application.mainClassName))
        baseName = RELEASE_ARTIFACT
        version = RELEASE_VERSION
        classifier = null
    }

    withType<com.hendraanggrian.packr.PackTask> {
        dependsOn("installDist")
    }
}