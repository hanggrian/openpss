private const val VERSION_KTOR = "1.2.4"

fun Dependencies.ktor(module: String) =
    "io.ktor:ktor-$module:$VERSION_KTOR"
