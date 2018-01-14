package com.wijayaprinting.io

import com.wijayaprinting.data.Language
import javafx.beans.property.StringProperty

open class PreferencesFile : PropertiesFile(".preferences",
        "employee" to "",
        "language" to Language.LOCALE_EN
) {
    companion object : PreferencesFile()

    val employee: StringProperty by map
    val language: StringProperty by map
}