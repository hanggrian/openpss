package com.hendraanggrian.openpss.content

import com.fatboyindustrial.gsonjodatime.Converters
import com.google.gson.GsonBuilder

object GsonBuilders {

    fun registerJodaTime(builder: GsonBuilder): GsonBuilder = builder.also {
        Converters.registerLocalDate(it)
        Converters.registerLocalTime(it)
        Converters.registerLocalDateTime(it)
        Converters.registerDateTime(it)
    }
}