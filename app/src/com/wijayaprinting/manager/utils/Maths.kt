@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.manager.utils

import org.apache.commons.math3.util.Precision.round

inline val Double.round: Double get() = round(this, 2)