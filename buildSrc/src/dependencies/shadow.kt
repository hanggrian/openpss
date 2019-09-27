private const val VERSION_SHADOW = "4.0.1"

fun Dependencies.shadow() =
    "com.github.jengelman.gradle.plugins:shadow:$VERSION_SHADOW"

val Plugins.shadow
    get() = id("com.github.johnrengelman.shadow")