pluginManagement.repositories {
    gradlePluginPortal()
    mavenCentral()
}
dependencyResolutionManagement.repositories {
    mavenCentral()
    maven("https://repository.jetbrains.com/kotlin-nosql")
}

rootProject.name = "openpss"

include("openpss")
include("website")
