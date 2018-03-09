package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Configs : DocumentSchema<Config>("config", Config::class) {
    val key = string("key")
    val value = string("value")
}

open class Config(
    val key: String,
    val value: String
) : Document<Configs>() {
    override lateinit var id: Id<String, Configs>

    val valueList: List<String> get() = value.split(", ")

    companion object {
        const val TIMEZONE_CONTINENT = "timezone_continent"
        const val TIMEZONE_CONTINENT_DEFAULT = "Asia"

        const val TIMEZONE_COUNTRIES = "timezone_country"
        const val TIMEZONE_COUNTRIES_DEFAULT = "Bangkok, Hanoi, Jakarta"
    }
}