package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.Setupable
import com.hendraanggrian.openpss.util.isEmpty
import kotlinx.nosql.Id
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import kotlin.reflect.KProperty

object GlobalSettings : DocumentSchema<GlobalSetting>("global_settings", GlobalSetting::class), Setupable {
    val key = string("key")
    val value = string("value")

    val LANGUAGE = "language" to "en-US" // or equivalent to Language.EN_US.fullCode
    val INVOICE_HEADERS = "invoice_headers" to ""

    override fun setup(wrapper: SessionWrapper) = wrapper.run {
        listOf(LANGUAGE, INVOICE_HEADERS)
            .filter { pair -> GlobalSettings { it.key.equal(pair.first) }.isEmpty() }
            .forEach { GlobalSettings += GlobalSetting(it.first, it.second) }
    }
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

    override lateinit var id: Id<String, GlobalSettings>

    inline val valueList: List<String> get() = value.split("|")
}