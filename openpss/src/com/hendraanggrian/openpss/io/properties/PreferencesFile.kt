package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.content.Language
import com.hendraanggrian.openpss.content.Language.EN_US
import com.hendraanggrian.openpss.ui.wage.readers.EClockingReader

/** User manually configurable settings file. */
object PreferencesFile : PropertiesFile("settings") {

    var LANGUAGE: String by EN_US.fullCode

    var WAGE_READER: String by EClockingReader.name

    var language: Language
        get() = Language.ofFullCode(LANGUAGE)
        set(value) {
            LANGUAGE = value.fullCode
        }
}