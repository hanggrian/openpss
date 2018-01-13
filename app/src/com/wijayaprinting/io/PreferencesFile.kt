package com.wijayaprinting.io

import com.wijayaprinting.data.Language
import javafx.beans.property.StringProperty

open class PreferencesFile : PropertiesFile(".preferences",
        "employee" to "",
        "language" to Language.LOCALE_EN
) {
    companion object : PreferencesFile()

    private val _employee: StringProperty by map
    private val _language: StringProperty by map

    val employeeProperty: StringProperty get() = _employee
    var employee: String
        get() = employeeProperty.get()
        set(value) = employeeProperty.set(value)

    val languageProperty: StringProperty get() = _language
    var language: String
        get() = languageProperty.get()
        set(value) = languageProperty.set(value)
}