package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object GlobalSettings : DocumentSchema<GlobalSetting>("global_settings", GlobalSetting::class) {
    val key = string("key")
    val value = string("value")
}

data class GlobalSetting(
    val key: String,
    var value: String
) : Document<GlobalSettings> {
    companion object {
        const val KEY_LANGUAGE = "language"
        const val KEY_INVOICE_HEADERS = "invoice_headers"

        fun listKeys(): List<String> = listOf(
            KEY_LANGUAGE,
            KEY_INVOICE_HEADERS)

        fun new(key: String): GlobalSetting = GlobalSetting(key, "")
    }

    override lateinit var id: Id<String, GlobalSettings>

    inline val valueList: List<String> get() = value.split("|")
}