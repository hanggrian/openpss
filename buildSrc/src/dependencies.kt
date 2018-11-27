import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec

fun DependencyHandler.android() = "com.android.tools.build:gradle:$VERSION_ANDROID_PLUGIN"
fun PluginDependenciesSpec.android(submodule: String) = id("com.android.$submodule")

fun DependencyHandler.androidx(
    repository: String,
    module: String = repository,
    version: String = VERSION_ANDROIDX
): String = "androidx.$repository:$module:$version"

fun DependencyHandler.material() = "com.google.android.material:material:$VERSION_ANDROIDX"

fun DependencyHandler.kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$it" }.orEmpty()}"

fun DependencyHandler.dokka() = "org.jetbrains.dokka:dokka-gradle-plugin:$VERSION_DOKKA"
inline val PluginDependenciesSpec.dokka get() = id("org.jetbrains.dokka")

fun DependencyHandler.controlsFx() = "org.controlsfx:controlsfx:$VERSION_CONTROLSFX"

fun DependencyHandler.apache(module: String, version: String) = "org.apache.${module.split("-")[0]}:$module:$version"
fun DependencyHandler.commonsValidator() = "commons-validator:commons-validator:$VERSION_COMMONS_VALIDATOR"

fun DependencyHandler.square(module: String, version: String, repo: String? = null) =
    optionalRepo("com.squareup", module, version, repo)

fun DependencyHandler.google(module: String, version: String, repo: String? = null) =
    optionalRepo("com.google", module, version, repo ?: module)

fun DependencyHandler.hendraanggrian(
    repository: String,
    module: String = repository,
    version: String
): String = "com.hendraanggrian.$repository:$module:$version"

fun PluginDependenciesSpec.generating(id: String) = id("com.hendraanggrian.generating.$id")

inline val PluginDependenciesSpec.packr get() = id("com.hendraanggrian.packr")

fun DependencyHandler.guava() = "com.google.guava:guava:$VERSION_GUAVA-jre"

fun DependencyHandler.jodaTime() = "joda-time:joda-time:$VERSION_JODA"

fun DependencyHandler.log4j12() = "org.slf4j:slf4j-log4j12:$VERSION_LOG4J12"

fun DependencyHandler.shadow() = "com.github.jengelman.gradle.plugins:shadow:$VERSION_SHADOW"
inline val PluginDependenciesSpec.shadow get() = id("com.github.johnrengelman.shadow")

fun DependencyHandler.testFx(module: String) = "org.testfx:testfx-$module:$VERSION_TESTFX"

fun DependencyHandler.gitPublish() = "org.ajoberstar:gradle-git-publish:$VERSION_GIT_PUBLISH"
inline val PluginDependenciesSpec.`git-publish` get() = id("org.ajoberstar.git-publish")

private fun optionalRepo(
    group: String,
    module: String,
    version: String,
    repo: String?
) = "$group${repo?.let { ".$it" }.orEmpty()}:$module:$version"