package com.hendraanggrian.openpss.io.properties

/** User manually configurable settings file. */
object SettingsFile : PropertiesFile("settings") {

    var INVOICE_QUICK_SELECT_CUSTOMER: Boolean by true
}