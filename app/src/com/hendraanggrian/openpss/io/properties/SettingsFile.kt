package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.ui.wage.readers.EClockingReader

/** User manually configurable settings file. */
object SettingsFile : PropertiesFile("settings") {

    var INVOICE_QUICK_SELECT_CUSTOMER: Boolean by true

    var WAGE_READER: String by EClockingReader.name
}