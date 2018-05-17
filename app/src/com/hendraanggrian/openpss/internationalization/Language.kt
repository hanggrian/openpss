package com.hendraanggrian.openpss.internationalization

import sun.util.locale.LocaleUtils
import java.util.Currency
import java.util.Locale
import java.util.ResourceBundle

enum class Language(private val nativeLocale: Locale) {
    EN_US(Locale.US),
    ID_ID(Locale("id", "ID"));

    val code: String
        get() = LocaleUtils.toLowerString(nativeLocale.language).intern().let {
            when (it) {
                "iw" -> "he"
                "ji" -> "yi"
                "in" -> "id"
                else -> it
            }
        }

    val fullCode: String get() = "$code-${nativeLocale.country}"

    fun toLocale(): Locale = nativeLocale

    @JvmOverloads
    fun toString(showCurrency: Boolean = false): String = nativeLocale.getDisplayLanguage(nativeLocale).let {
        when {
            showCurrency -> "${Currency.getInstance(nativeLocale).symbol} - $it"
            else -> it
        }
    }

    fun toResourcesBundle(): ResourceBundle = ResourceBundle.getBundle("string_$code")

    companion object {
        fun of(fullCode: String): Language = Language.values().singleOrNull { it.fullCode == fullCode } ?: EN_US
    }
}