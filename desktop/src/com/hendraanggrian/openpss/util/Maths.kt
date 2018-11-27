@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import org.apache.commons.math3.util.Precision

/** The only reason why Apache Commons Math is used. */
inline fun Double.round(): Double = Precision.round(this, 2)