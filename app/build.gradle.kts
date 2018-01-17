import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.plugin.android.AndroidGradleWrapper.srcDir

plugins {
    java
    kotlin("jvm")
    rsync
    buildconfig
}

java.sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDirs("res", "licenses")
    }
    getByName("test") {
        java.srcDir("tests/src")
        resources.srcDir("tests/res")
    }
}

rsync {
    srcDir("src")
    resDir("res")
    packageName("com.wijayaprinting")
    leadingSlash(true)
    debug(isDebug)
}

buildconfig {
    srcDir("src")
    packageName("com.wijayaprinting")
    version(releaseVersion)
    debug(isDebug)
}

dependencies {
    implementation(project(":scene"))

    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotlin("nosql-mongodb", nosqlMongodbVersion))

    implementation(rx("javafx", rxjavafxVersion))
    implementation(rx("kotlin", rxkotlinVersion))

    implementation(apache("commons", "lang3", commonsLangVersion))
    implementation(apache("commons", "math3", commonsMathVersion))
    implementation(apache("poi", "ooxml", poiVersion))

    implementation(google("guava", guavaVersion))
    implementation(slf4j("log4j12", log4j12Version))

    testImplementation(junit(junitVersion))
}