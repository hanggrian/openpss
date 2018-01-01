
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec

const val releaseGroup = "com.wijayaprinting"
const val releaseArtifact = "manager"
const val releaseVersion = "0.5"

/** Set false when building app, true when building stage. */
const val isDebug = true

const val kotlinVersion = "1.2.10"

const val dataVersion = "0.5"
const val kotfxVersion = "0.13"
const val guavaVersion = "23.6-jre"
const val rxjavafxVersion = "2.2.2"
const val poiVersion = "3.17"
const val commonsLangVersion = "3.7"
const val commonsValidatorVersion = "1.6"
const val commonsMathVersion = "3.6.1"
const val slf4jVersion = "1.7.25"

const val junitVersion = "4.12"

fun DependencyHandler.hendraanggrian(module: String, version: String) = "com.hendraanggrian:$module:$version"
inline val PluginDependenciesSpec.rsync get() = id("rsync")
inline val PluginDependenciesSpec.buildconfig get() = id("buildconfig")

fun DependencyHandler.rx(module: String, version: String) = "io.reactivex.rxjava2:rx$module:$version"
fun DependencyHandler.apache(module1: String, module2: String, version: String) = "org.apache.$module1:$module1-$module2:$version"
fun DependencyHandler.commonsValidator(version: String) = "commons-validator:commons-validator:$version"
fun DependencyHandler.google(module: String, version: String) = "com.google.$module:$module:$version"
fun DependencyHandler.wp(module: String, version: String) = "com.wijayaprinting:$module:$version"
fun DependencyHandler.slf4j(module: String, version: String) = "org.slf4j:slf4j-$module:$version"
fun DependencyHandler.junit(version: String) = "junit:junit:$version"