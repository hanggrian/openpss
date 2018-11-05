package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.content.Language
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.util.isEmpty
import kotlinx.nosql.Id
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import kotlin.reflect.KProperty

object GlobalSettings : DocumentSchema<GlobalSetting>("global_settings", GlobalSetting::class) {
    val key = string("key")
    val value = string("value")
}

data class GlobalSetting(
    val key: String,
    var value: String
) : Document<GlobalSettings> {

    companion object {
        private val language = "language" to Language.EN_US.fullCode
        private val invoiceHeaders = "invoice_headers" to ""

        val KEY_LANGUAGE by language
        val KEY_INVOICE_HEADERS by invoiceHeaders

        fun setupDefault(wrapper: SessionWrapper) = wrapper.run {
            listOf(language, invoiceHeaders)
                .filter { pair -> GlobalSettings { it.key.equal(pair.first) }.isEmpty() }
                .forEach { GlobalSettings += GlobalSetting(it.first, it.second) }
        }

        private operator fun Pair<String, String>.getValue(
            thisRef: Any?,
            property: KProperty<*>
        ): String = first
    }

    override lateinit var id: Id<String, GlobalSettings>

    inline val valueList: List<String> get() = value.split("|")
}