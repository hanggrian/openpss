package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Configs : DocumentSchema<Config>("configs", Config::class) {
    val key = string("key")
    val value = string("value")
}

data class Config(
    val key: String,
    var value: String
) : Document<Configs> {

    override lateinit var id: Id<String, Configs>

    companion object {
        const val KEY_CURRENCY_LANGUAGE = "currency_language"
        const val KEY_CURRENCY_COUNTRY = "currency_country"

        fun listKeys(): List<String> = listOf(KEY_CURRENCY_LANGUAGE, KEY_CURRENCY_COUNTRY)

        fun new(key: String, value: String = ""): Config = Config(key, value)
    }
}