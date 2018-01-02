package com.wijayaprinting.manager.io

import com.wijayaprinting.manager.internal.Language
import javafx.beans.property.StringProperty

open class PreferencesFile : PropertiesFile(".preferences") {
    companion object : PreferencesFile()

    val employee: StringProperty by map
    val language: StringProperty by map

    override val pairs: List<Pair<String, String>>
        get() = listOf(
                "employee" to "",
                "language" to Language.LOCALE_EN
        )
}