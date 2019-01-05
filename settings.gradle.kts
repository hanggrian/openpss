include("core")
include("server")
includeAll("client")
include("website")

fun includeAll(projectPath: String) = rootDir.listFiles()
    .filter { it.name.startsWith(projectPath) && it.isDirectory }
    .forEach { include(it.name) }