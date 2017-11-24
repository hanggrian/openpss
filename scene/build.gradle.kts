import org.gradle.kotlin.dsl.kotlin

plugins {
    `java-library`
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib", kotlinVersion))
    compile(kotfx(kotfxVersion))
    compile("com.wijayaprinting:mysql:$mysqlVersion")
    compile("org.apache.commons:commons-lang3:$commonsLangVersion")
    compile("commons-validator:commons-validator:$commonsValidatorVersion")
}