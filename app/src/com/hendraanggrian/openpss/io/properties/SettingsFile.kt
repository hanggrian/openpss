package com.hendraanggrian.openpss.io.properties

import javafx.beans.property.StringProperty

/** User manually configurable settings file. */
class SettingsFile : PropertiesFile("settings") {

    override val pairs: Array<Pair<String, Any>>
        get() = arrayOf("invoice_search_customer" to false)

    val employee: StringProperty by this
}