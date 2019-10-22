package com.hendraanggrian.openpss

import com.fatboyindustrial.gsonjodatime.Converters
import com.google.gson.GsonBuilder

@Suppress("SpellCheckingInspection")
fun GsonBuilder.registerJodaTime(): GsonBuilder = also {
    Converters.registerLocalDate(it)
    Converters.registerLocalTime(it)
    Converters.registerLocalDateTime(it)
    Converters.registerDateTime(it)
}
