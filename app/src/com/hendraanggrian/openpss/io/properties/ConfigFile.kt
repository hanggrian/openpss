package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.Language
import javafx.beans.property.StringProperty

/** Properties file for general settings. */
object ConfigFile : PropertiesFile("config") {

    override val pairs: Array<Pair<String, String>>
        get() = arrayOf(
            "employee" to "",
            "language" to Language.ENGLISH.code
        )

    val employee: StringProperty by this
    val language: StringProperty by this
}