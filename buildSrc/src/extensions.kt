/** Set false when building app, true when building stage. */
const val isDebug = true
const val releaseVersion = "0.7"

const val kotlinVersion = "1.2.10"
const val nosqlMongodbVersion = "0.1-SNAPSHOT"

fun Dependency.apache(module1: String, module2: String, version: String) = "org.apache.$module1:$module1-$module2:$version"
fun Dependency.commonsValidator(version: String) = "commons-validator:commons-validator:$version"
const val commonsLangVersion = "3.7"
const val commonsMathVersion = "3.6.1"
const val commonsValidatorVersion = "1.6"
const val poiVersion = "3.17"

fun Dependency.google(module: String, version: String) = "com.google.$module:$module:$version"
const val guavaVersion = "23.6-jre"

fun Dependency.hendraanggrian(module: String, version: String) = "com.hendraanggrian:$module:$version"
const val kotfxVersion = "0.18"
inline val Plugin.rsync get() = id("rsync")
inline val Plugin.buildconfig get() = id("buildconfig")

fun Dependency.joda(version: String) = "joda-time:joda-time:$version"
const val jodaVersion = "2.9.9"

fun Dependency.rx(module: String, version: String) = "io.reactivex.rxjava2:rx$module:$version"
const val rxjavafxVersion = "2.2.2"
const val rxkotlinVersion = "2.2.0"

fun Dependency.junit(version: String) = "junit:junit:$version"
const val junitVersion = "4.12"

private typealias Dependency = org.gradle.api.artifacts.dsl.DependencyHandler
private typealias Plugin = org.gradle.plugin.use.PluginDependenciesSpec