private const val VERSION_LOGBACK = "1.2.3"

fun Dependencies.logback(module: String) =
    "ch.qos.logback:logback-$module:$VERSION_LOGBACK"