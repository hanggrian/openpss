package com.wijayaprinting.io.properties

import com.wijayaprinting.Language
import javafx.beans.property.StringProperty

/** Properties file for general settings. */
object ConfigFile : PropertiesFile("config") {

    override val pairs: Array<Pair<String, String>>
        get() = arrayOf(
            "employee" to "",
            "language" to Language.ENGLISH.locale
        )

    val employee: StringProperty by this
    val language: StringProperty by this
}