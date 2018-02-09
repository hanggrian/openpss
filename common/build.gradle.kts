import org.gradle.kotlin.dsl.kotlin

group = releaseGroup
version = releaseVersion

plugins {
    `java-library`
    kotlin("jvm")
}

java.sourceSets.getByName("main").java.srcDir("src")

dependencies {
    compile(kotlin("stdlib", kotlinVersion))
    compile(jodaTime())
}