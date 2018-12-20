package com.hendraanggrian.openpss.i18n

import sun.util.locale.LocaleUtils
import java.util.Currency
import java.util.Locale
import java.util.ResourceBundle

enum class Language(private val nativeLocale: Locale) {
    EN_US(Locale.US),
    ID_ID(Locale("id", "ID"));

    /** Reverse the damage done in [Locale.convertOldISOCodes]. */
    val code: String
        get() = LocaleUtils.toLowerString(nativeLocale.language)
            .intern()
            .let {
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
    fun toString(showCurrency: Boolean = false): String = nativeLocale
        .getDisplayLanguage(nativeLocale)
        .let {
            when {
                showCurrency -> "${Currency.getInstance(nativeLocale).symbol} - $it"
                else -> it
            }
        }

    fun toResourcesBundle(): ResourceBundle = ResourceBundle.getBundle("string_$code")

    companion object {

        fun ofCode(code: String): Language =
            find { it.code == code }

        fun ofFullCode(fullCode: String): Language =
            find { it.fullCode == fullCode }

        private inline fun find(predicate: (Language) -> Boolean): Language = Language.values()
            .singleOrNull(predicate) ?: EN_US
    }
}