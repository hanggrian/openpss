import org.gradle.kotlin.dsl.kotlin

group = releaseGroup
version = releaseVersion

plugins {
    `java-library`
    kotlin("jvm")
}

java.sourceSets {
    getByName("main").java.srcDir("src")
}

dependencies {
    compile(project(":common"))
    compile(kotlin("stdlib", kotlinVersion))
    compile(hendraanggrian("kotfx", kotfxVersion))
    compile(commonsValidator())
}