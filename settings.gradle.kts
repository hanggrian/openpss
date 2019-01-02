include("core")
includeAll("client")
include("server")
include("website")

fun includeAll(projectPath: String) = rootDir.listFiles()
    .filter { it.name.startsWith(projectPath) && it.isDirectory }
    .forEach { include(it.name) }