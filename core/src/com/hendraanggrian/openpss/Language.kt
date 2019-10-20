package com.hendraanggrian.openpss

import java.util.Currency
import java.util.Locale
import java.util.ResourceBundle

/**
 * List of supported languages and their JVM implementation.
 * There's a separate module for this enum since it is unused in Android.
 *
 * @param nativeLocale following pattern of [Regex.nativePattern]
 */
enum class Language(private val nativeLocale: Locale) {
    ENGLISH(Locale.US),
    INDONESIAN(Locale("id"));

    /** Reverse the damage done in [Locale.convertOldISOCodes]. */
    val code: String get() = nativeLocale.language

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

    fun toResourcesBundle(): ResourceBundle = ResourceBundle.getBundle("string_${nativeLocale.language}")

    companion object {

        fun ofCode(code: String): Language = find { it.code == code }

        fun ofFullCode(fullCode: String): Language = find { it.fullCode == fullCode }

        private inline fun find(predicate: (Language) -> Boolean): Language =
            Language.values().singleOrNull(predicate) ?: ENGLISH
    }
}
