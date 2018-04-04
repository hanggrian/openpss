package com.hendraanggrian.openpss.io.properties

import javafx.beans.property.StringProperty
import java.util.Locale.US

/** Properties file for general settings. */
object ConfigFile : PropertiesFile("config") {

    override val pairs: Array<Pair<String, String>>
        get() = arrayOf(
            "employee" to "",
            "language" to US.language
        )

    val employee: StringProperty by this
    val language: StringProperty by this
}