import org.gradle.kotlin.dsl.kotlin

plugins {
    java
    kotlin("jvm")
    id("rsync")
    id("buildconfig")
}

rsync {
    packageName("com.wijayaprinting.manager")
    leadingSlash(true)
    debug(isDebug)
}

buildconfig {
    packageName("com.wijayaprinting.manager")
    groupId(releaseGroup)
    artifactId(releaseArtifact)
    version(releaseVersion)
    debug(isDebug)
}

dependencies {
    implementation(project(":scene"))
    implementation(kotlin("stdlib", kotlinVersion))

    implementation(rx("javafx", rxjavafxVersion))
    implementation(hendraanggrian("rxexposed", rxexposedVersion))

    implementation(apache("commons", "math3", commonsMathVersion))
    implementation(apache("poi", "ooxml", poiVersion))

    implementation(google("guava", guavaVersion))
    implementation(slf4j("log4j12", slf4jVersion))

    testImplementation(junit(junitVersion))
}