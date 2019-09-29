package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.nosql.Document
import com.hendraanggrian.openpss.nosql.Schema
import com.hendraanggrian.openpss.nosql.StringId
import kotlin.reflect.KProperty
import kotlinx.nosql.string

object GlobalSettings : Schema<GlobalSetting>("global_settings", GlobalSetting::class) {
    val key = string("key")
    val value = string("value")

    val LANGUAGE = "language" to "en-US" // or equivalent to Language.EN_US.fullCode
    val INVOICE_HEADERS = "invoice_headers" to ""
}

data class GlobalSetting(
    val key: String,
    var value: String
) : Document<GlobalSettings> {

    companion object {

        val KEY_LANGUAGE by GlobalSettings.LANGUAGE
        val KEY_INVOICE_HEADERS by GlobalSettings.INVOICE_HEADERS

        private operator fun Pair<String, String>.getValue(
            thisRef: Any?,
            property: KProperty<*>
        ): String = first
    }

    override lateinit var id: StringId<GlobalSettings>

    inline val valueList: List<String> get() = value.split("|")
}
