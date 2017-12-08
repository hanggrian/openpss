import org.gradle.kotlin.dsl.kotlin

plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib", kotlinVersion))
    compile(apache("commons", "lang3", commonsLangVersion))
    compile(wp("data", dataVersion))
    compile(hendraanggrian("kotfx", kotfxVersion))
    compile(commonsValidator(commonsValidatorVersion))
}