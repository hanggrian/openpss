package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.ui.wage.readers.EClockingReader

/** User manually configurable settings file. */
object ReaderFile : PropertiesFile("reader") {

    var WAGE_READER: String by EClockingReader.name
}