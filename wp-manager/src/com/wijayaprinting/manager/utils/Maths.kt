@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.manager.utils

import org.apache.commons.math3.util.Precision.round
import java.lang.Math.abs

inline val Double.round: Double get() = round(this, 2)

inline val Int.abs: Int get() = abs(this)