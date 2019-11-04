const val VERSION_KTFX = "8.6.1"
const val VERSION_PREFS = "0.1"

const val VERSION_PIKASSO = "0.2"
const val VERSION_RECYCLERVIEW_PAGINATED = "0.2"

const val VERSION_BUNDLER = "0.3-rc1"
const val VERSION_PLUGIN_R = "0.1"
const val VERSION_PLUGIN_BUILDCONFIG = "0.1"
const val VERSION_PLUGIN_LOCALE = "0.1"
const val VERSION_PLUGIN_PACKR = "0.1"

fun Dependencies.hendraanggrian(module: String, version: String) =
    "com.hendraanggrian:$module:$version"

fun Dependencies.hendraanggrian(repo: String, module: String, version: String) =
    "com.hendraanggrian.$repo:$module:$version"

fun Plugins.hendraanggrian(module: String) =
    id("com.hendraanggrian.$module")