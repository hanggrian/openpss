includeAll("core")
includeAll("client")
include("server")
include("website")

fun includeAll(projectPath: String) = include(*rootDir.listFiles()
    .filter { it.name.startsWith(projectPath) && it.isDirectory && it.name != "client-android" }
    .map { it.name }
    .toTypedArray())