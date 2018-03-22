package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.transaction
import kotlinx.nosql.Id
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import java.util.Locale

object Configs : DocumentSchema<Config>("config", Config::class) {
    val key = string("key")
    val value = string("value")
}

data class Config(
    val key: String,
    var value: String = ""
) : Document<Configs> {

    override lateinit var id: Id<String, Configs>

    companion object {
        private const val KEY_CURRENCY_LANGUAGE = "currency_language"
        private const val KEY_CURRENCY_COUNTRY = "currency_country"

        fun getKeys(): Array<String> = arrayOf(KEY_CURRENCY_LANGUAGE, KEY_CURRENCY_COUNTRY)

        fun getCurrencyLocale(): Locale {
            var language: String? = null
            var country: String? = null
            transaction {
                language = Configs.find { key.equal(KEY_CURRENCY_LANGUAGE) }.singleOrNull()?.value
                country = Configs.find { key.equal(KEY_CURRENCY_COUNTRY) }.singleOrNull()?.value
            }
            return when {
                language != null && country != null -> Locale(language, country)
                else -> Locale.getDefault()
            }
        }
    }
}