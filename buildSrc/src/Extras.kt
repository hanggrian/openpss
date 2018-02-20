const val releaseGroup = "com.wijayaprinting"
const val releaseArtifact = "wijayaprinting"
const val releaseDebug = false
const val releaseVersion = "0.9"

const val kotlinVersion = "1.2.21"
const val nosqlVersion = "0.1-SNAPSHOT"
const val coroutinesVersion = "0.22.2"
const val spekVersion = "1.1.5"

const val rVersion = "0.2"
const val buildconfigVersion = "0.8"
const val kotfxVersion = "0.28"
const val commonsLangVersion = "3.7"
const val poiVersion = "3.17"
const val junitPlatformVersion = "1.0.0"

fun Dependency.kotlinx(module: String, version: String? = null) = "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" }
    ?: ""}"

fun Dependency.apache(module: String, version: String) = "org.apache.${module.split("-")[0]}:$module:$version"
fun Dependency.commonsValidator() = "commons-validator:commons-validator:1.6"

fun Dependency.guava() = "com.google.guava:guava:24.0-jre"

fun Dependency.hendraanggrian(module: String, version: String) = "com.hendraanggrian:$module:$version"
inline val Plugin.r get() = id("r")
inline val Plugin.buildconfig get() = id("buildconfig")

fun Dependency.jodaTime() = "joda-time:joda-time:2.9.9"

fun Dependency.log4j12() = "org.slf4j:slf4j-log4j12:1.7.25"

fun Dependency.shadow() = "com.github.jengelman.gradle.plugins:shadow:2.0.2"
inline val Plugin.shadow get() = id("com.github.johnrengelman.shadow")

fun Dependency.ktlint(): org.gradle.api.artifacts.Dependency = add("ktlint", "com.github.shyiko:ktlint:0.15.0")

fun Dependency.junitPlatform(module: String, version: String) = "org.junit.platform:junit-platform-$module:$version"
val Plugin.`junit-platform` get() = id("org.junit.platform.gradle.plugin")

fun Dependency.spek(module: String, version: String) = "org.jetbrains.spek:spek-$module:$version"

private typealias Dependency = org.gradle.api.artifacts.dsl.DependencyHandler
private typealias Plugin = org.gradle.plugin.use.PluginDependenciesSpec
