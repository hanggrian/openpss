package com.hendraanggrian.openpss

import org.joda.time.DateTime

const val PATTERN_DATE = "MM/dd/yyyy" // to comply with JavaFX's DatePicker
const val PATTERN_TIME = "HH:mm"
const val PATTERN_DATETIME = "$PATTERN_DATE $PATTERN_TIME"
const val PATTERN_DATETIME_EXTENDED = "$PATTERN_DATE EEE $PATTERN_TIME"

val START_OF_TIME = DateTime(0)