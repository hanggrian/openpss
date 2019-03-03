const val VERSION_GRADLE = "5.2.1"

const val VERSION_KOTLIN = "1.3.21"
const val VERSION_COROUTINES = "1.1.1"
const val VERSION_NOSQL = "0.1-SNAPSHOT"
internal const val VERSION_DOKKA = "0.9.17"
internal const val VERSION_KTOR = "1.1.3"

const val SDK_MIN = 14
const val SDK_TARGET = 28

internal const val VERSION_ANDROID_PLUGIN = "3.5.0-alpha06"
const val VERSION_MULTIDEX = "2.0.1"
const val VERSION_ANDROIDX = "1.1.0"
const val VERSION_ESPRESSO = "3.1.1"
const val VERSION_RUNNER = "1.1.1"
const val VERSION_RULES = "1.1.1"

const val VERSION_KTFX = "8.4.9"
const val VERSION_DEFAULTS = "0.3"
const val VERSION_PIKASSO = "0.2"
const val VERSION_RECYCLERVIEW_PAGINATED = "0.2"
const val VERSION_BUNDLER = "0.3-rc1"
const val VERSION_PLUGIN_R = "0.1"
const val VERSION_PLUGIN_BUILDCONFIG = "0.1"
const val VERSION_PLUGIN_PACKR = "0.1"

const val VERSION_GSON = "2.8.5"
const val VERSION_GUAVA = "27.0.1"
const val VERSION_MAVEN = "3.6.0"
const val VERSION_COMMONS_LANG = "3.8.1"
const val VERSION_COMMONS_MATH = "3.6.1"
const val VERSION_POI = "4.0.1"

const val VERSION_LOGBACK = "1.2.3"
const val VERSION_MONGODB = "3.9.1"
const val VERSION_COMMONS_VALIDATOR = "1.6"
const val VERSION_JODA_TIME = "2.10.1"
const val VERSION_JODA_TIME_GSON_SERIALIZERS = "1.6.0"
internal const val VERSION_SLF4J = "1.7.25"
internal const val VERSION_SHADOW = "4.0.1"
internal const val VERSION_TESTFX = "4.0.15-alpha"
internal const val VERSION_GIT_PUBLISH = "0.3.3"
internal const val VERSION_KTLINT = "0.30.0"

fun String.stable(): String = substringBefore('-')