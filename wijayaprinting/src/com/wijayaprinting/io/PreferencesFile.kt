package com.wijayaprinting.io

import com.wijayaprinting.data.Language
import javafx.beans.property.StringProperty

open class PreferencesFile : PropertiesFile(".preferences",
        "employee" to "",
        "language" to Language.LOCALE_EN
) {
    companion object : PreferencesFile()

    val employeeProperty: StringProperty by map
    var employee: String
        get() = employeeProperty.get()
        set(value) = employeeProperty.set(value)

    val languageProperty: StringProperty by map
    var language: String
        get() = languageProperty.get()
        set(value) = languageProperty.set(value)
}