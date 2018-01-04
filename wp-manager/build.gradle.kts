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
    packageName("$releaseGroup.$releaseArtifact")
    leadingSlash(true)
    debug(isDebug)
}

buildconfig {
    srcDir("src")
    packageName("$releaseGroup.$releaseArtifact")
    groupId(releaseGroup)
    artifactId(releaseArtifact)
    version(releaseVersion)
    debug(isDebug)
}

dependencies {
    implementation(project(":wp-manager-scene"))
    implementation(kotlin("stdlib", kotlinVersion))

    implementation(apache("commons", "lang3", commonsLangVersion))
    implementation(apache("commons", "math3", commonsMathVersion))
    implementation(apache("poi", "ooxml", poiVersion))

    implementation(rx("javafx", rxjavafxVersion))
    implementation(google("guava", guavaVersion))
    implementation(slf4j("log4j12", slf4jVersion))

    testImplementation(junit(junitVersion))
}