package com.wijayaprinting.io

import com.wijayaprinting.Language
import javafx.beans.property.StringProperty

open class ConfigFile : PropertiesFile(".config",
        "employee" to "",
        "language" to Language.ENGLISH.locale
) {
    companion object : ConfigFile()

    val employee: StringProperty by map
    val language: StringProperty by map
}