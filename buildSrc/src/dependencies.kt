import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec

fun DependencyHandler.ktor(module: String) = "io.ktor:ktor-$module:$VERSION_KTOR"

fun DependencyHandler.mongodb() = "org.mongodb:mongo-java-driver:$VERSION_MONGODB"

fun DependencyHandler.kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$it" }.orEmpty()}"

fun DependencyHandler.dokka(module: String? = null) =
    "org.jetbrains.dokka:dokka-${module?.let { "$it-" }.orEmpty()}gradle-plugin:$VERSION_DOKKA"

fun PluginDependenciesSpec.dokka(module: String? = null) = id("org.jetbrains.dokka${module?.let { "-$it" }.orEmpty()}")

fun DependencyHandler.android() = "com.android.tools.build:gradle:$VERSION_ANDROID_PLUGIN"
fun PluginDependenciesSpec.android(submodule: String) = id("com.android.$submodule")

fun DependencyHandler.androidx(
    repository: String,
    module: String = repository,
    version: String = VERSION_ANDROIDX
): String = "androidx.$repository:$module:$version"

fun DependencyHandler.material() = "com.google.android.material:material:$VERSION_ANDROIDX"

fun DependencyHandler.hendraanggrian(
    repository: String,
    module: String = repository,
    version: String
): String = "com.hendraanggrian.$repository:$module:$version"

fun DependencyHandler.jakeWharton(
    repo: String?,
    module: String,
    version: String
): String = "com.jakewharton${repo?.let { ".$it" } ?: ""}:$module:$version"

fun DependencyHandler.controlsFx() = "org.controlsfx:controlsfx:$VERSION_CONTROLSFX"

fun DependencyHandler.apache(module: String, version: String) = "org.apache.${module.split("-")[0]}:$module:$version"
fun DependencyHandler.commonsValidator() = "commons-validator:commons-validator:$VERSION_COMMONS_VALIDATOR"

fun DependencyHandler.square(module: String, version: String, repo: String? = null) =
    optionalRepo("com.squareup", module, version, repo)

fun DependencyHandler.google(module: String, version: String, repo: String? = null) =
    optionalRepo("com.google", module, version, repo ?: module)

fun PluginDependenciesSpec.generating(id: String) = id("com.hendraanggrian.generating.$id")

inline val PluginDependenciesSpec.packr get() = id("com.hendraanggrian.packr")

fun DependencyHandler.guava() = "com.google.guava:guava:$VERSION_GUAVA-jre"

fun DependencyHandler.jodaTime() = "joda-time:joda-time:$VERSION_JODA"

fun DependencyHandler.slf4j(module: String) = "org.slf4j:slf4j-$module:$VERSION_SLF4J"

fun DependencyHandler.shadow() = "com.github.jengelman.gradle.plugins:shadow:$VERSION_SHADOW"
inline val PluginDependenciesSpec.shadow get() = id("com.github.johnrengelman.shadow")

fun DependencyHandler.testFx(module: String) = "org.testfx:testfx-$module:$VERSION_TESTFX"

fun DependencyHandler.gitPublish() = "org.ajoberstar:gradle-git-publish:$VERSION_GIT_PUBLISH"
inline val PluginDependenciesSpec.`git-publish` get() = id("org.ajoberstar.git-publish")

fun DependencyHandler.junit() = "junit:junit:$VERSION_JUNIT"

private fun optionalRepo(
    group: String,
    module: String,
    version: String,
    repo: String?
) = "$group${repo?.let { ".$it" }.orEmpty()}:$module:$version"