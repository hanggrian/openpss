package com.wijayaprinting.util

import org.apache.commons.math3.util.Precision.round

inline val Double.rounded: Double get() = round(this, 2)