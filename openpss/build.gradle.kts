import com.helger.css.ECSSVersion
import com.helger.css.reader.CSSReader
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.nio.charset.StandardCharsets
import java.util.Properties

val developerId: String by project
val developerName: String by project
val releaseGroup: String by project
val releaseVersion: String by project
val releaseArtifact: String by project
val releaseUrl: String by project
val distributionName: String by project
val distributionFullName: String by project
val distributionDebug: String by project

val jdkVersion = JavaLanguageVersion.of(libs.versions.jdk.get())
val jreVersion = JavaLanguageVersion.of(libs.versions.jre.get())

val javaModules = listOf("java.logging", "java.security.sasl", "jdk.crypto.ec")
val javafxModules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.swing")
val javaArguments =
    listOf(
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-exports=javafx.base/com.sun.javafx.binding=ALL-UNNAMED",
        "--add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED",
        "--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED",
        "--add-exports=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
        "--add-exports=javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED",
        "--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
        "--add-exports=javafx.graphics/com.sun.javafx.print=ALL-UNNAMED",
    )

plugins {
    alias(libs.plugins.javafx)
    kotlin("jvm") version libs.versions.kotlin
    application
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.packaging)
    alias(libs.plugins.ktlint)
}
buildscript {
    dependencies.classpath(libs.ph.css)
}

javafx {
    version = libs.versions.javafx.get()
    modules = javafxModules
}

kotlin.jvmToolchain(jdkVersion.asInt())

ktlint.version = libs.versions.ktlint.get()

application {
    applicationName = distributionName
    mainClass.set("$releaseGroup.$releaseArtifact.OpenPssApp")
    applicationDefaultJvmArgs = javaArguments
}

packaging {
    modules.set(javaModules + javafxModules)
    javaArgs.set(javaArguments)
    verbose.set(true)
    windows {
        modulePaths.set(listOf(File("C:/JavaFX/javafx-jmods-${libs.versions.javafx.get()}")))
        icon.set(projectDir.resolve("icons/$distributionName.ico"))
    }
    mac {
        modulePaths.set(listOf(File("/Library/JavaFX/javafx-jmods-${libs.versions.javafx.get()}")))
        icon.set(projectDir.resolve("icons/$distributionName.icns"))
    }
}

dependencies {
    ktlintRuleset(libs.rulebook.ktlint)

    implementation(kotlin("reflect", libs.versions.kotlin.get()))
    implementation(libs.kotlinx.nosql.mongodb)
    implementation(libs.kotlinx.coroutines)

    implementation(libs.mongodb.java.driver)
    implementation(libs.joda.time)

    implementation(libs.gson)
    implementation(libs.guava)
    implementation(libs.maven.artifact)
    implementation(libs.commons.lang3)
    implementation(libs.commons.math3)
    implementation(libs.commons.validator)
    implementation(libs.poi.ooxml)

    implementation(libs.bundles.ktfx)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.slf4j)

    testImplementation(kotlin("test-junit", libs.versions.kotlin.get()))
    testImplementation(libs.bundles.testfx)
    testImplementation(libs.truth)
}

buildConfig {
    useJavaOutput()
    buildConfigField("AUTHOR", developerId)
    buildConfigField("NAME", distributionName)
    buildConfigField("FULL_NAME", distributionFullName)
    buildConfigField("VERSION", releaseVersion)
    buildConfigField("ARTIFACT", releaseArtifact)
    buildConfigField("EMAIL", "$developerId@proton.me")
    buildConfigField("WEBSITE", releaseUrl)
    buildConfigField<Boolean>("DEBUG", distributionDebug.toBoolean())
}

tasks {
    compileJava {
        options.release = jreVersion.asInt()
    }
    compileKotlin {
        compilerOptions.jvmTarget
            .set(JvmTarget.fromTarget(JavaVersion.toVersion(jreVersion).toString()))
    }
    test {
        jvmArgs(*javaArguments.toTypedArray())
    }

    val r = buildConfig.forClass("R")
    val generateR by registering {
        val resources = sourceSets["main"].resources.asFileTree
        inputs.files(resources)
        doFirst {
            resources.visit {
                path
                    .lowercase()
                    .replace("\\W".toRegex(), "_")
                    .replace("_${file.extension}", "")
                    .let { key ->
                        if (key !in r.buildConfigFields.map { it.name }) {
                            r.buildConfigField(key, "/$path")
                        }
                    }
                when (file.extension) {
                    "properties" ->
                        file
                            .inputStream()
                            .use { stream ->
                                Properties()
                                    .apply { load(stream) }
                                    .keys
                                    .forEach { value ->
                                        var parentName = file.parentFile!!.nameWithoutExtension
                                        if (parentName == "resources") {
                                            parentName = "string"
                                        }
                                        val key = "${parentName}_$value"
                                        if (key !in r.buildConfigFields.map { it.name }) {
                                            r.buildConfigField(key, value.toString())
                                        }
                                    }
                            }
                    "css" ->
                        CSSReader
                            .readFromFile(file, StandardCharsets.UTF_8, ECSSVersion.CSS30)!!
                            .allStyleRules
                            .flatMap { it.allSelectors }
                            .mapNotNull {
                                val member =
                                    it
                                        .getMemberAtIndex(0)
                                        ?.asCSSString
                                        ?: return@mapNotNull null
                                when {
                                    member.startsWith('.') -> member.substringAfter('.')
                                    member.startsWith('#') -> member.substringAfter('#')
                                    member == "*" -> null
                                    else -> member
                                }
                            }.forEach { member ->
                                val key = "style_${member.replace('-', '_')}"
                                if (key !in r.buildConfigFields.map { it.name }) {
                                    r.buildConfigField(key, member)
                                }
                            }
                }
            }
        }
    }
    generateBuildConfig {
        dependsOn(generateR)
    }
}
