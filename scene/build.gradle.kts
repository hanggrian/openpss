import org.gradle.kotlin.dsl.kotlin

plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib", kotlinVersion))
    compile(wp("data", dataVersion))
    compile(hendraanggrian("kotfx", kotfxVersion))
    compile(commonsValidator(commonsValidatorVersion))
}