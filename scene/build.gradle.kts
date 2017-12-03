import org.gradle.kotlin.dsl.kotlin

plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib", kotlinVersion))
    compile(apache("commons", "lang3", commonsLangVersion))
    compile("com.wijayaprinting:data:$dataVersion")
    compile(hendraanggrian("kotfx", kotfxVersion))
    compile("commons-validator:commons-validator:$commonsValidatorVersion")
}