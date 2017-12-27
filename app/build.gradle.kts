import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.plugin.android.AndroidGradleWrapper.srcDir

plugins {
    java
    kotlin("jvm")
    id("rsync")
    id("buildconfig")
}

java.sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("res")
    }
    getByName("test") {
        java.srcDir("tests/src")
        resources.srcDir("tests/res")
    }
}

rsync {
    srcDir("src")
    resDir("res")
    packageName("com.wijayaprinting.manager")
    leadingSlash(true)
    debug(isDebug)
}

buildconfig {
    srcDir("src")
    packageName("com.wijayaprinting.manager")
    groupId(releaseGroup)
    artifactId(releaseArtifact)
    version(releaseVersion)
    debug(isDebug)
}

dependencies {
    implementation(project(":scene"))
    implementation(kotlin("stdlib", kotlinVersion))

    implementation(hendraanggrian("rxexposed", rxexposedVersion))

    implementation(apache("commons", "lang3", commonsLangVersion))
    implementation(apache("commons", "math3", commonsMathVersion))
    implementation(apache("poi", "ooxml", poiVersion))

    implementation(rx("javafx", rxjavafxVersion))
    implementation(google("guava", guavaVersion))
    implementation(slf4j("log4j12", slf4jVersion))

    testImplementation(junit(junitVersion))
}