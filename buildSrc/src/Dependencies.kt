import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec

fun DependencyHandler.kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$it" }.orEmpty()}"

fun DependencyHandler.apache(module: String, version: String) = "org.apache.${module.split("-")[0]}:$module:$version"
fun DependencyHandler.commonsValidator() = "commons-validator:commons-validator:$VERSION_COMMONS_VALIDATOR"

fun DependencyHandler.hendraanggrian(module: String, version: String, repo: String? = null) =
    "com.hendraanggrian${repo?.let { ".$it" }.orEmpty()}:$module:$version"

inline val PluginDependenciesSpec.r get() = id("r")
inline val PluginDependenciesSpec.buildconfig get() = id("buildconfig")
inline val PluginDependenciesSpec.packr get() = id("packr")

fun DependencyHandler.guava() = "com.google.guava:guava:$VERSION_GUAVA-jre"

fun DependencyHandler.jodaTime() = "joda-time:joda-time:$VERSION_JODA"

fun DependencyHandler.log4j12() = "org.slf4j:slf4j-log4j12:$VERSION_LOG4J12"

fun DependencyHandler.shadow() = "com.github.jengelman.gradle.plugins:shadow:$VERSION_SHADOW"
inline val PluginDependenciesSpec.shadow get() = id("com.github.johnrengelman.shadow")

fun DependencyHandler.junitPlatform(module: String) = "org.junit.platform:junit-platform-$module:$VERSION_JUNIT_PLATFORM"
val PluginDependenciesSpec.`junit-platform` get() = id("org.junit.platform.gradle.plugin")

fun DependencyHandler.spek(module: String) = "org.jetbrains.spek:spek-$module:$VERSION_SPEK"

fun DependencyHandler.testFX(module: String) = "org.testfx:testfx-$module:$VERSION_TESTFX"

fun DependencyHandler.ktlint() = "com.github.shyiko:ktlint:$VERSION_KTLINT"