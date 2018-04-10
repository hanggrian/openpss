package com.hendraanggrian.openpss.io.properties

import javafx.beans.property.StringProperty

/** User manually configurable settings file. */
object SettingsFile : PropertiesFile("settings") {

    val INVOICE_SHOW_CUSTOMER: StringProperty by true
    inline val invoiceShowCustomer: Boolean get() = INVOICE_SHOW_CUSTOMER.value.toBoolean()
}