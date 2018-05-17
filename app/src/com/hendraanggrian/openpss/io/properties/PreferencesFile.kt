package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.internationalization.Language
import com.hendraanggrian.openpss.internationalization.Language.EN_US
import com.hendraanggrian.openpss.ui.wage.readers.EClockingReader

/** User manually configurable settings file. */
object PreferencesFile : PropertiesFile("preferences") {

    var LANGUAGE: String by EN_US.fullCode

    var INVOICE_QUICK_SELECT_CUSTOMER: Boolean by true

    var WAGE_READER: String by EClockingReader.name

    var language: Language
        get() = Language.of(LANGUAGE)
        set(value) {
            LANGUAGE = value.fullCode
        }
}