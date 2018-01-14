/** Set false when building app, true when building stage. */
const val isDebug = false
const val releaseVersion = "0.6"

const val kotlinVersion = "1.2.10"

const val rxjavafxVersion = "2.2.2"
const val rxkotlinVersion = "2.2.0"

const val poiVersion = "3.17"
const val commonsLangVersion = "3.7"
const val commonsValidatorVersion = "1.6"
const val commonsMathVersion = "3.6.1"

const val jodaVersion = "2.9.9"
const val nosqlMongodbVersion = "0.1-SNAPSHOT"
const val kotfxVersion = "0.18"
const val guavaVersion = "23.6-jre"
const val slf4jVersion = "1.7.25"

const val junitVersion = "4.12"

fun Dependency.exposed(version: String) = "org.jetbrains.exposed:exposed:$version"

fun Dependency.rx(module: String, version: String) = "io.reactivex.rxjava2:rx$module:$version"

fun Dependency.joda(version: String) = "joda-time:joda-time:$version"
fun Dependency.apache(module1: String, module2: String, version: String) = "org.apache.$module1:$module1-$module2:$version"
fun Dependency.commonsValidator(version: String) = "commons-validator:commons-validator:$version"
fun Dependency.google(module: String, version: String) = "com.google.$module:$module:$version"
fun Dependency.slf4j(module: String, version: String) = "org.slf4j:slf4j-$module:$version"
fun Dependency.hendraanggrian(module: String, version: String) = "com.hendraanggrian:$module:$version"
inline val Plugin.rsync get() = id("rsync")
inline val Plugin.buildconfig get() = id("buildconfig")

fun Dependency.junit(version: String) = "junit:junit:$version"

private typealias Dependency = org.gradle.api.artifacts.dsl.DependencyHandler
private typealias Plugin = org.gradle.plugin.use.PluginDependenciesSpec