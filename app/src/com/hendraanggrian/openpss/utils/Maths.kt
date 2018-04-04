package com.hendraanggrian.openpss.utils

import java.lang.Double.isInfinite
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_HALF_UP
import kotlin.Double.Companion.NaN

/** Stolen from apache-commons-math3's Precision. */
fun Double.round(): Double = try {
    BigDecimal(java.lang.Double.toString(this))
        .setScale(2, ROUND_HALF_UP)
        .toDouble()
        .let { rounded ->
            // MATH-1089: negative values rounded to zero should result in negative zero
            if (rounded == 0.0) 0.0 * this else rounded
        }
} catch (ex: NumberFormatException) {
    if (isInfinite(this)) this else NaN
}