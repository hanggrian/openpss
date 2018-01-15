package com.wijayaprinting.util

import org.apache.commons.math3.util.Precision.round

/** Round this decimal with default scale. */
inline val Double.rounded: Double get() = round(this, 2)