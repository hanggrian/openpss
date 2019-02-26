import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec

fun DependencyHandler.ktor(module: String) = "io.ktor:ktor-$module:$VERSION_KTOR"

fun DependencyHandler.kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version.wrap { ":$it" }}"

fun DependencyHandler.dokka(module: String? = null) =
    "org.jetbrains.dokka:dokka-${module.wrap { "$it-" }}gradle-plugin:$VERSION_DOKKA"

fun PluginDependenciesSpec.dokka(module: String? = null) =
    id("org.jetbrains.dokka${module.wrap { "-$it" }}")

fun DependencyHandler.android() = "com.android.tools.build:gradle:$VERSION_ANDROID_PLUGIN"
fun PluginDependenciesSpec.android(submodule: String) = id("com.android.$submodule")

fun DependencyHandler.androidx(
    repository: String,
    module: String = repository,
    version: String = VERSION_ANDROIDX
): String = "androidx.$repository:$module:$version"

fun DependencyHandler.material(version: String = VERSION_ANDROIDX) =
    "com.google.android.material:material:$version"

fun DependencyHandler.hendraanggrian(module: String, version: String): String =
    "com.hendraanggrian:$module:$version"

fun DependencyHandler.hendraanggrian(
    repository: String,
    module: String,
    version: String
): String = "com.hendraanggrian.$repository:$module:$version"

fun DependencyHandler.apache(module: String, version: String) =
    "org.apache.${module.split("-")[0]}:$module:$version"

fun DependencyHandler.google(repo: String? = null, module: String, version: String) =
    "com.google${repo.wrap { ".$it" }}:$module:$version"

inline val PluginDependenciesSpec.packr get() = id("com.hendraanggrian.packr")

fun DependencyHandler.slf4j(module: String) = "org.slf4j:slf4j-$module:$VERSION_SLF4J"

fun DependencyHandler.shadow() = "com.github.jengelman.gradle.plugins:shadow:$VERSION_SHADOW"
inline val PluginDependenciesSpec.shadow get() = id("com.github.johnrengelman.shadow")

fun DependencyHandler.testFx(module: String) = "org.testfx:testfx-$module:$VERSION_TESTFX"

fun DependencyHandler.gitPublish() = "org.ajoberstar:gradle-git-publish:$VERSION_GIT_PUBLISH"
inline val PluginDependenciesSpec.`git-publish` get() = id("org.ajoberstar.git-publish")

private fun String?.wrap(wrapper: (String) -> String) = this?.let(wrapper).orEmpty()