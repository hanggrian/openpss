package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.nosql.Document
import com.hendraanggrian.openpss.nosql.StringId
import com.hendraanggrian.openpss.schema.GlobalSettings
import kotlin.reflect.KProperty

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