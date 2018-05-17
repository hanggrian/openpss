package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.ui.wage.readers.EClockingReader

/** User manually configurable settings file. */
object PreferencesFile : PropertiesFile("preferences") {

    var INVOICE_QUICK_SELECT_CUSTOMER: Boolean by true

    var WAGE_READER: String by EClockingReader.name
}