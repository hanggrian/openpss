@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import com.fatboyindustrial.gsonjodatime.Converters
import com.google.gson.GsonBuilder

inline fun GsonBuilder.jodaTimeSupport(): GsonBuilder {
    Converters.registerLocalDate(this)
    Converters.registerLocalTime(this)
    Converters.registerLocalDateTime(this)
    Converters.registerDateTime(this)
    return this
}