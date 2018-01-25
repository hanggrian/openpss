const val releaseGroup = "com.wijayaprinting"
const val releaseArtifact = "wijayaprinting"
const val releaseDebug = false
const val releaseVersion = "0.7"

const val kotlinVersion = "1.2.21"
const val nosqlVersion = "0.1-SNAPSHOT"

fun Dependency.apache(module: String, version: String) = "org.apache.${module.split("-")[0]}:$module:$version"
val Dependency.`commons-validator` get() = "commons-validator:commons-validator:1.6"
const val commonsLangVersion = "3.7"
const val commonsMathVersion = "3.6.1"
const val poiVersion = "3.17"

val Dependency.guava get() = "com.google.guava:guava:23.6-jre"

fun Dependency.hendraanggrian(module: String, version: String) = "com.hendraanggrian:$module:$version"
inline val Plugin.rsync get() = id("rsync")
inline val Plugin.buildconfig get() = id("buildconfig")
const val rsyncVersion = "0.8"
const val buildconfigVersion = "0.6"
const val kotfxVersion = "0.19"

val Dependency.`joda-time` get() = "joda-time:joda-time:2.9.9"

fun Dependency.rx(module: String, version: String) = "io.reactivex.rxjava2:rx$module:$version"
const val rxjavafxVersion = "2.2.2"
const val rxkotlinVersion = "2.2.0"

val Dependency.log4j12 get() = "org.slf4j:slf4j-log4j12:1.7.25"

fun Dependency.junitPlatform(module: String, version: String) = "org.junit.platform:junit-platform-$module:$version"
val Plugin.`junit-platform` get() = id("org.junit.platform.gradle.plugin")
const val junitPlatformVersion = "1.0.0"

fun Dependency.spek(module: String, version: String) = "org.jetbrains.spek:spek-$module:$version"
const val spekVersion = "1.1.5"

private typealias Dependency = org.gradle.api.artifacts.dsl.DependencyHandler
private typealias Plugin = org.gradle.plugin.use.PluginDependenciesSpec