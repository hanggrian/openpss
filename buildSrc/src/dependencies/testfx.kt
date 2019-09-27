private const val VERSION_TESTFX = "4.0.15-alpha"

fun Dependencies.testFx(module: String) =
    "org.testfx:testfx-$module:$VERSION_TESTFX"
