package com.hanggrian.openpss

import com.hanggrian.openpss.Language.Locales.toLowerString
import com.hanggrian.openpss.db.schemas.GlobalSetting
import com.hanggrian.openpss.db.transaction
import java.util.Currency
import java.util.Locale
import java.util.ResourceBundle

enum class Language(private val nativeLocale: Locale) {
    EN_US(Locale.US),
    ID_ID(Locale("id", "ID")),
    ;

    /** Reverse the damage done in [Locale.convertOldISOCodes]. */
    val code: String
        get() =
            nativeLocale.language
                .toLowerString()
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

    fun toResourcesBundle(): ResourceBundle = ResourceBundle.getBundle("string_$code")

    fun toString(showCurrency: Boolean = false): String =
        toString().let {
            when {
                showCurrency -> "${Currency.getInstance(nativeLocale).symbol} - $it"
                else -> it
            }
        }

    override fun toString(): String = nativeLocale.getDisplayLanguage(nativeLocale)

    companion object {
        fun ofCode(code: String): Language = find { it.code == code }

        fun ofFullCode(fullCode: String): Language = find { it.fullCode == fullCode }

        fun ofServer(): Language =
            ofFullCode(
                transaction {
                    findGlobalSettings(GlobalSetting.KEY_LANGUAGE).single().value
                },
            )

        private inline fun find(predicate: (Language) -> Boolean) =
            Language.entries.singleOrNull(predicate) ?: EN_US
    }

    /**
     * @see LocaleUtils
     */
    private object Locales {
        fun String.toLowerString(): String {
            val len = length
            var idx = 0
            while (idx < len) {
                if (get(idx).isUpper()) {
                    break
                }
                idx++
            }
            if (idx == len) {
                return this
            }

            val buf = CharArray(len)
            for (i in 0 until len) {
                val c = get(i)
                buf[i] = if ((i < idx)) c else c.lower()
            }
            return String(buf)
        }

        private fun Char.isUpper() = this in 'A'..'Z'

        private fun Char.lower() = if (isUpper()) (code + 0x20).toChar() else this
    }
}
