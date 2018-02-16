/**
 * Stolen from apache-commons-math3's Precision.
 */

package com.wijayaprinting.util

import java.lang.Double.isInfinite
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_HALF_UP
import kotlin.Double.Companion.NaN

private const val POSITIVE_ZERO = 0.0

fun round(
    x: Double,
    scale: Int = 2,
    roundingMethod: Int = ROUND_HALF_UP
): Double = try {
    BigDecimal(java.lang.Double.toString(x))
        .setScale(scale, roundingMethod)
        .toDouble()
        .let { rounded ->
            // MATH-1089: negative values rounded to zero should result in negative zero
            if (rounded == POSITIVE_ZERO) POSITIVE_ZERO * x else rounded
        }
} catch (ex: NumberFormatException) {
    if (isInfinite(x)) x else NaN
}