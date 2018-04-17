package com.hendraanggrian.openpss.io.properties

/** User manually configurable settings file. */
object SettingsFile : PropertiesFile("settings") {

    var CUSTOMER_PAGINATION_ITEMS: Int by 20

    var INVOICE_PAGINATION_ITEMS: Int by 20
    var INVOICE_QUICK_SELECT_CUSTOMER: Boolean by true
}