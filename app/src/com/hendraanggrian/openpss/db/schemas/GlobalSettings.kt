package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.DocumentSchemaQueryWrapper
import kotlinx.nosql.Id
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.string

object GlobalSettings : DocumentSchema<GlobalSetting>("global_settings", GlobalSetting::class) {
    val key = string("key")
    val value = string("value")

    const val KEY_CURRENCY_LANGUAGE = "currency_language"
    const val KEY_CURRENCY_COUNTRY = "currency_country"
    const val KEY_INVOICE_HEADERS = "invoice_headers"

    fun listKeys(): List<String> = listOf(
        KEY_CURRENCY_LANGUAGE, KEY_CURRENCY_COUNTRY, KEY_INVOICE_HEADERS)

    fun new(key: String): GlobalSetting = GlobalSetting(key, "")
}

data class GlobalSetting(
    val key: String,
    var value: String
) : Document<GlobalSettings> {

    override lateinit var id: Id<String, GlobalSettings>

    inline val valueList: List<String> get() = value.split("|")
}

@Suppress("NOTHING_TO_INLINE")
inline fun MongoDBSession.findGlobalSettings(
    key: String
): DocumentSchemaQueryWrapper<GlobalSettings, String, GlobalSetting> = GlobalSettings.find { this.key.equal(key) }