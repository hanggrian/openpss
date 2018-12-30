package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.nosql.Document
import com.hendraanggrian.openpss.schema.Settings
import kotlinx.nosql.Id
import kotlin.reflect.KProperty

data class Setting(
    val key: String,
    var value: String
) : Document<Settings> {

    companion object {

        val KEY_LANGUAGE by Settings.LANGUAGE
        val KEY_INVOICE_HEADERS by Settings.INVOICE_HEADERS

        private operator fun Pair<String, String>.getValue(
            thisRef: Any?,
            property: KProperty<*>
        ): String = first
    }

    override lateinit var id: Id<String, Settings>

    inline val valueList: List<String> get() = value.split("|")
}