const val VERSION_KOTLIN = "1.3.70"
const val VERSION_COROUTINES = "1.3.5"
const val VERSION_NOSQL = "0.1-SNAPSHOT"

fun Dependencies.kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$it" }.orEmpty()}"