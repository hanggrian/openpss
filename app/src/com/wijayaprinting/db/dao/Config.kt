package com.wijayaprinting.db.dao

import com.wijayaprinting.db.Ided
import com.wijayaprinting.db.schema.Configs
import kotlinx.nosql.Id

data class Config(
    val key: String,
    val value: String
) : Ided<Configs> {
    override lateinit var id: Id<String, Configs>

    companion object {
        const val TIMEZONE_CONTINENT = "timezone_continent"
        const val TIMEZONE_CONTINENT_DEFAULT = "Asia"

        const val TIMEZONE_COUNTRIES = "timezone_country"
        const val TIMEZONE_COUNTRIES_DEFAULT = "Bangkok, Hanoi, Jakarta"
    }

    val valueList: List<String> get() = value.split(", ")
}