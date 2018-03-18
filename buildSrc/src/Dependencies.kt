import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec

fun DependencyHandler.kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

fun DependencyHandler.apache(module: String, version: String) = "org.apache.${module.split("-")[0]}:$module:$version"
fun DependencyHandler.commonsValidator() = "commons-validator:commons-validator:$commonsValidatorVersion"

fun DependencyHandler.guava() = "com.google.guava:guava:$guavaVersion-jre"

fun DependencyHandler.hendraanggrian(module: String, version: String, repo: String? = null) =
    "com.hendraanggrian${repo?.let { ".$it" } ?: ""}:$module:$version"

inline val PluginDependenciesSpec.r get() = id("r")
inline val PluginDependenciesSpec.buildconfig get() = id("buildconfig")
inline val PluginDependenciesSpec.packr get() = id("packr")

fun DependencyHandler.jodaTime() = "joda-time:joda-time:$jodaVersion"

fun DependencyHandler.log4j12() = "org.slf4j:slf4j-log4j12:$log4j12Version"

fun DependencyHandler.shadow() = "com.github.jengelman.gradle.plugins:shadow:$shadowVersion"
inline val PluginDependenciesSpec.shadow get() = id("com.github.johnrengelman.shadow")

fun DependencyHandler.junitPlatform(module: String) = "org.junit.platform:junit-platform-$module:$junitPlatformVersion"
val PluginDependenciesSpec.`junit-platform` get() = id("org.junit.platform.gradle.plugin")

fun DependencyHandler.spek(module: String) = "org.jetbrains.spek:spek-$module:$spekVersion"

fun DependencyHandler.testFX(module: String) = "org.testfx:testfx-$module:$textFXVersion"

fun DependencyHandler.ktlint() = "com.github.shyiko:ktlint:$ktlintVersion"