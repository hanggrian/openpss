package com.hendraanggrian.openpss.util

import org.apache.commons.math3.util.Precision.round

fun Double.round(): Double = round(this, 2)