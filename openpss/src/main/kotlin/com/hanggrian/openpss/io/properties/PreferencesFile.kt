@file:Suppress("ktlint:standard:property-naming")

package com.hanggrian.openpss.io.properties

import com.hanggrian.openpss.Language
import com.hanggrian.openpss.ui.wage.readers.EClockingReader

/** User manually configurable settings file. */
object PreferencesFile : PropertiesFile("settings") {
    var LANGUAGE: String by Language.EN_US.code

    var WAGE_READER: String by EClockingReader.name

    var language: Language
        get() = Language.ofCode(LANGUAGE)
        set(value) {
            LANGUAGE = value.code
        }

    private fun readResolve(): Any = LoginFile
}
