const val VERSION_MAVEN = "3.6.0"
const val VERSION_COMMONS_MATH = "3.6.1"
const val VERSION_POI = "4.1.0"
const val VERSION_COMMONS_VALIDATOR = "1.6"

fun Dependencies.apache(module: String, version: String): String {
    require('-' in module) { "Module must contain `-` (e.g.: commons-lang, commons-math)." }
    return "org.apache.${module.split('-').first()}:$module:$version"
}