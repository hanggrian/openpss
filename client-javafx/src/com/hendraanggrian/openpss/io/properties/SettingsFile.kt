package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.content.Language
import com.hendraanggrian.openpss.content.Language.EN_US

/** User manually configurable settings file. */
object SettingsFile : PropertiesFile("settings") {

    var EMPLOYEE: String by ""
    var LANGUAGE: String by EN_US.fullCode

    var language: Language
        get() = Language.ofFullCode(LANGUAGE)
        set(value) {
            LANGUAGE = value.fullCode
        }
}