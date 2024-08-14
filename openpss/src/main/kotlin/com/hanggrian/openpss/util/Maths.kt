package com.hanggrian.openpss.util

import org.apache.commons.math3.util.Precision

/** The only reason why Apache Commons Math is used. */
inline fun Double.round(): Double = Precision.round(this, 2)
