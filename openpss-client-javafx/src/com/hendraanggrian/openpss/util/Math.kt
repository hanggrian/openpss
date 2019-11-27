package com.hendraanggrian.openpss.util

import org.apache.commons.math3.util.Precision

/** The only reason why Apache Commons Math is used. */
fun Double.round(): Double = Precision.round(this, 2)
