package com.hendraanggrian.openpss

import com.fatboyindustrial.gsonjodatime.Converters
import com.google.gson.GsonBuilder

fun GsonBuilder.registerJodaTimeSerializers(): GsonBuilder = also {
    Converters.registerLocalDate(it)
    Converters.registerLocalTime(it)
    Converters.registerLocalDateTime(it)
    Converters.registerDateTime(it)
}
