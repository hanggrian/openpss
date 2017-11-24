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

    implementation("io.reactivex.rxjava2:rxjava:$rxjavaVersion")
    implementation("io.reactivex.rxjava2:rxjavafx:$rxkotlinVersion")
    implementation("io.reactivex.rxjava2:rxkotlin:$rxkotlinVersion")

    implementation("com.wijayaprinting:mysql:$mysqlVersion")
    implementation("com.hendraanggrian:rxexposed:$rxexposedVersion")
    implementation("com.google.guava:guava:$guavaVersion-jre")
    implementation("org.apache.commons:commons-math3:$commonsMathVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
    implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")

    testImplementation(junit(junitVersion))
}