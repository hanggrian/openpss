@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.utils

import kotlinx.nosql.AbstractColumn
import kotlinx.nosql.AbstractSchema
import kotlinx.nosql.Query

/** Matches with [regex] automatically transformed to pattern with certain [flags]. */
inline fun <T : AbstractSchema, C> AbstractColumn<out C?, T, *>.matches(
    regex: Any,
    flags: Int = 0
): Query = matches(regex.toString().toPattern(flags))