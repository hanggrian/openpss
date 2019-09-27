private const val VERSION_SLF4J = "1.7.28"

fun Dependencies.slf4j(module: String) =
    "org.slf4j:slf4j-$module:$VERSION_SLF4J"