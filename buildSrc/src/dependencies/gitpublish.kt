private const val VERSION_GIT_PUBLISH = "3.0.0"

fun Dependencies.gitPublish() = "org.ajoberstar:gradle-git-publish:$VERSION_GIT_PUBLISH"

val Plugins.`git-publish` get() = id("org.ajoberstar.git-publish")
