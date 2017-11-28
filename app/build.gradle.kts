import org.gradle.kotlin.dsl.kotlin

plugins {
    java
    kotlin("jvm")
    id("rsync")
    id("buildconfig")
}

rsync {
    packageName = "com.wijayaprinting.javafx"
    leadingSlash = true
    debug = isDebug
}

buildconfig {
    packageName = "com.wijayaprinting.javafx"
    groupId(releaseGroup)
    artifactId(releaseArtifact)
    version(releaseVersion)
    debug(isDebug)
}

dependencies {
    implementation(project(":scene"))

    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotfx(kotfxVersion))

    implementation(rx("java", rxjavaVersion))
    implementation(rx("javafx", rxkotlinVersion))
    implementation(rx("kotlin", rxkotlinVersion))

    implementation(apache("commons", "math3", commonsMathVersion))
    implementation(apache("poi", "ooxml", poiVersion))

    implementation("com.wijayaprinting:mysql:$mysqlVersion")
    implementation("com.hendraanggrian:rxexposed:$rxexposedVersion")
    implementation("com.google.guava:guava:$guavaVersion-jre")
    implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")

    testImplementation(junit(junitVersion))
}