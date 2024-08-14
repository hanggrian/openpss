import java.nio.charset.StandardCharsets
import java.util.Properties

val developerId: String by project
val developerName: String by project
val releaseGroup: String by project
val releaseVersion: String by project
val releaseArtifact: String by project
val releaseUrl: String by project

plugins {
    alias(libs.plugins.javafx)
    kotlin("jvm") version libs.versions.kotlin
    application
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.localization)
    alias(libs.plugins.packaging)
    alias(libs.plugins.ktlint)
}
buildscript {
    dependencies.classpath(libs.ph.css)
}

javafx {
    version = "${libs.versions.jdk.get()}.0.9"
    modules("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.swing")
}

kotlin.jvmToolchain(libs.versions.jdk.get().toInt())

ktlint.version = libs.versions.ktlint.get()

application {
    applicationName = "OpenPSS"
    mainClass.set("$releaseGroup.$releaseArtifact.OpenPssApp")
}

packaging {
    icon.set(projectDir.resolve("logo_mac.icns"))
    verbose.set(true)
}

dependencies {
    ktlintRuleset(libs.rulebook.ktlint)

    implementation(libs.kotlinx.nosql.mongodb)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.gson)
    implementation(libs.guava)

    implementation(libs.joda.time)
    implementation(libs.maven.artifact)
    implementation(libs.commons.lang3)
    implementation(libs.commons.math3)
    implementation(libs.commons.validator)
    implementation(libs.poi.ooxml)

    implementation(libs.bundles.ktfx)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.log4j)

    testImplementation(kotlin("test-junit", libs.versions.kotlin.get()))
    testImplementation(kotlin("reflect", libs.versions.kotlin.get()))
    testImplementation(libs.bundles.testfx)
    testImplementation(libs.truth)
}

buildConfig {
    useJavaOutput()
    buildConfigField("NAME", application.applicationName)
    buildConfigField("VERSION", releaseVersion)
    buildConfigField("ARTIFACT", releaseArtifact)
    buildConfigField("EMAIL", "$developerId@proton.me")
    buildConfigField("WEBSITE", releaseUrl)

    buildConfigField<Boolean>("DEBUG", true)

    buildConfigField("AUTHOR", developerId)
    buildConfigField("FULL_NAME", developerName)
}

val r = buildConfig.forClass("R")
val generateR by tasks.registering {
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
                            val properties = Properties().apply { load(stream) }
                            properties.keys
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
                    com.helger.css.reader.CSSReader
                        .readFromFile(
                            file,
                            StandardCharsets.UTF_8,
                            com.helger.css.ECSSVersion.CSS30,
                        )!!
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

tasks {
    withType<JavaExec> {
        jvmArgs(
            "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
            "--add-exports=javafx.base/com.sun.javafx.binding=ALL-UNNAMED",
            "--add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED",
            "--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED",
            "--add-exports=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
            "--add-exports=javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED",
            "--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
        )
    }
    test {
        jvmArgs("--add-opens=java.base/java.lang.reflect=ALL-UNNAMED")
    }
    generateBuildConfig {
        dependsOn(generateR)
    }
}
