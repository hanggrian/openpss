import org.gradle.kotlin.dsl.kotlin

plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib", kotlinVersion))
    compile(kotfx(kotfxVersion))
    compile(apache("commons", "lang3", commonsLangVersion))
    compile("com.wijayaprinting:data:$dataVersion")
    compile("commons-validator:commons-validator:$commonsValidatorVersion")
}