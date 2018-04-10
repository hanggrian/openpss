package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.string

object GlobalSettings : DocumentSchema<GlobalSetting>("global_settings", GlobalSetting::class) {
    val key = string("key")
    val value = string("value")
}

data class GlobalSetting(
    val key: String,
    var value: String
) : Document<GlobalSettings> {

    override lateinit var id: Id<String, GlobalSettings>

    companion object {
        const val KEY_CURRENCY_LANGUAGE = "currency_language"
        const val KEY_CURRENCY_COUNTRY = "currency_country"
        const val KEY_INVOICE_TITLE = "invoice_title"
        const val KEY_INVOICE_SUBTITLE1 = "invoice_subtitle1"
        const val KEY_INVOICE_SUBTITLE2 = "invoice_subtitle2"
        const val KEY_INVOICE_SUBTITLE3 = "invoice_subtitle3"

        fun listKeys(): List<String> = listOf(
            KEY_CURRENCY_LANGUAGE, KEY_CURRENCY_COUNTRY,
            KEY_INVOICE_TITLE, KEY_INVOICE_SUBTITLE1, KEY_INVOICE_SUBTITLE2, KEY_INVOICE_SUBTITLE3)

        fun new(key: String): GlobalSetting = GlobalSetting(key, "")
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun MongoDBSession.findGlobalSettings(key: String): String = GlobalSettings.find { this.key.equal(key) }.single().value