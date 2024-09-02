package com.hanggrian.openpss

import com.hanggrian.openpss.db.schemas.GlobalSetting
import com.hanggrian.openpss.db.transaction
import java.util.Currency
import java.util.Locale
import java.util.ResourceBundle

enum class Language(private val nativeLocale: Locale) {
    EN_US(Locale.US),
    ID_ID(Locale("id", "ID")),
    ;

    val code: String get() = "${nativeLocale.language}-${nativeLocale.country}"

    fun toLocale(): Locale = nativeLocale

    fun toResourcesBundle(): ResourceBundle =
        ResourceBundle.getBundle("string_${nativeLocale.language}")

    fun toStringWithCurrency(): String =
        "${Currency.getInstance(nativeLocale).symbol} - ${toString()}"

    override fun toString(): String = nativeLocale.getDisplayLanguage(nativeLocale)

    companion object {
        fun ofCode(code: String): Language = find { it.code == code }

        fun ofServer(): Language =
            ofCode(
                transaction {
                    findGlobalSettings(GlobalSetting.KEY_LANGUAGE).single().value
                },
            )

        private inline fun find(predicate: (Language) -> Boolean) =
            Language.entries.singleOrNull(predicate) ?: EN_US
    }
}
