package com.hendraanggrian.openpss.internationalization

import sun.util.locale.LocaleUtils
import java.util.Locale

enum class Region(private val nativeLocale: Locale) {
    US(Locale.US),
    INDONESIA(Locale("id", "ID"));

    val language: String
        get() = LocaleUtils.toLowerString(nativeLocale.language).intern().let {
            when (it) {
                "iw" -> "he"
                "ji" -> "yi"
                "in" -> "id"
                else -> it
            }
        }

    val country: String get() = nativeLocale.country

    val displayLanguage: String get() = nativeLocale.getDisplayLanguage(nativeLocale)

    fun toLocale(): Locale = nativeLocale

    override fun toString(): String = displayLanguage

    companion object {
        fun from(language: String, country: String): Region = Region.values().single {
            it.language.equals(language, true) && it.country.equals(country, true)
        }
    }
}