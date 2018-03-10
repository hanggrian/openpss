import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec

const val releaseGroup = "com.hendraanggrian"
const val releaseArtifact = "openpss"
const val releaseName = "OpenPSS"
const val releaseDebug = false
const val releaseVersion = "0.12"

const val kotlinVersion = "1.2.30"
const val nosqlVersion = "0.1-SNAPSHOT"
const val coroutinesVersion = "0.22.4"

const val rVersion = "0.7"
const val buildconfigVersion = "0.11"
const val packrVersion = "0.3"
const val kotlinFXVersion = "0.1"
const val commonsLangVersion = "3.7"
const val poiVersion = "3.17"

fun DependencyHandler.kotlinx(module: String, version: String? = null) = "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" }
    ?: ""}"

fun DependencyHandler.apache(module: String, version: String) = "org.apache.${module.split("-")[0]}:$module:$version"
fun DependencyHandler.commonsValidator() = "commons-validator:commons-validator:1.6"

fun DependencyHandler.guava() = "com.google.guava:guava:24.0-jre"

fun DependencyHandler.hendraanggrian(repo: String?, module: String, version: String) = "com.hendraanggrian${repo?.let { ".$it" }
    ?: ""}:$module:$version"

inline val PluginDependenciesSpec.r get() = id("r")
inline val PluginDependenciesSpec.buildconfig get() = id("buildconfig")
inline val PluginDependenciesSpec.packr get() = id("packr")

fun DependencyHandler.jodaTime() = "joda-time:joda-time:2.9.9"

fun DependencyHandler.log4j12() = "org.slf4j:slf4j-log4j12:1.7.25"

fun DependencyHandler.shadow() = "com.github.jengelman.gradle.plugins:shadow:2.0.2"
inline val PluginDependenciesSpec.shadow get() = id("com.github.johnrengelman.shadow")

fun DependencyHandler.ktlint() = "com.github.shyiko:ktlint:0.19.0"

fun DependencyHandler.junitPlatform(module: String) = "org.junit.platform:junit-platform-$module:1.0.0"
val PluginDependenciesSpec.`junit-platform` get() = id("org.junit.platform.gradle.plugin")

fun DependencyHandler.spek(module: String) = "org.jetbrains.spek:spek-$module:1.1.5"