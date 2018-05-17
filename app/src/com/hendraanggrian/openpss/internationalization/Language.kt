package com.hendraanggrian.openpss.internationalization

import sun.util.locale.LocaleUtils
import java.util.Currency
import java.util.Locale

enum class Language(private val nativeLocale: Locale) {
    US(Locale.US),
    INDONESIA(Locale("id", "ID"));

    val code: String
        get() = LocaleUtils.toLowerString(nativeLocale.language).intern().let {
            when (it) {
                "iw" -> "he"
                "ji" -> "yi"
                "in" -> "id"
                else -> it
            }
        }

    val language: String get() = nativeLocale.getDisplayLanguage(nativeLocale)

    val currency: String get() = Currency.getInstance(nativeLocale).symbol

    fun toLocale(): Locale = nativeLocale

    override fun toString(): String = "$code-${nativeLocale.country}"

    companion object {
        fun of(language: String): Language = Language.values().singleOrNull {
            it.code == language.substringBefore('-')
        } ?: US
    }
}