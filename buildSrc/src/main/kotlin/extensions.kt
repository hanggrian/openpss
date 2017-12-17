import org.gradle.api.artifacts.dsl.DependencyHandler

const val releaseGroup = "com.wijayaprinting"
const val releaseArtifact = "manager"
const val releaseVersion = "0.3"

/** Set false when building app, true when building stage. */
const val isDebug = false

const val dataVersion = "0.3"
const val kotlinVersion = "1.2.10"
const val kotfxVersion = "0.11"
const val guavaVersion = "23.5-jre"
const val rxjavaVersion = "2.1.6"
const val rxkotlinVersion = "2.1.0"
const val rxexposedVersion = "0.1"
const val poiVersion = "3.16"
const val commonsLangVersion = "3.6"
const val commonsValidatorVersion = "1.6"
const val commonsMathVersion = "3.6.1"
const val slf4jVersion = "1.7.25"

const val junitVersion = "4.12"

fun DependencyHandler.rx(module: String, version: String) = "io.reactivex.rxjava2:rx$module:$version"
fun DependencyHandler.apache(module1: String, module2: String, version: String) = "org.apache.$module1:$module1-$module2:$version"
fun DependencyHandler.commonsValidator(version: String) = "commons-validator:commons-validator:$version"
fun DependencyHandler.google(module: String, version: String) = "com.google.$module:$module:$version"
fun DependencyHandler.wp(module: String, version: String) = "com.wijayaprinting:$module:$version"
fun DependencyHandler.hendraanggrian(module: String, version: String) = "com.hendraanggrian:$module:$version"
fun DependencyHandler.slf4j(module: String, version: String) = "org.slf4j:slf4j-$module:$version"
fun DependencyHandler.junit(version: String) = "junit:junit:$version"