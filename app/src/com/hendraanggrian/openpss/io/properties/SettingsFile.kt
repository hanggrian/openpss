package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.ui.wage.readers.EClockingReader

/** User manually configurable settings file. */
object SettingsFile : PropertiesFile("settings") {

    var CUSTOMER_PAGINATION_ITEMS: Int by 20

    var INVOICE_PAGINATION_ITEMS: Int by 20
    var INVOICE_QUICK_SELECT_CUSTOMER: Boolean by true

    var WAGE_READER: String by EClockingReader.name
}