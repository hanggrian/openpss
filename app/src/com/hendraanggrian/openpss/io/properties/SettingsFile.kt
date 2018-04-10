package com.hendraanggrian.openpss.io.properties

import javafx.beans.property.BooleanProperty

/** User manually configurable settings file. */
object SettingsFile : PropertiesFile("settings") {

    val INVOICE_QUICK_SELECT_CUSTOMER: BooleanProperty by true
}