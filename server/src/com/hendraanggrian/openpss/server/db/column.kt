package com.hendraanggrian.openpss.server.db

import kotlinx.nosql.AbstractColumn
import kotlinx.nosql.AbstractSchema
import kotlinx.nosql.Query

/** Matches with [regex] automatically transformed to pattern with certain [flags]. */
fun <T : AbstractSchema, C> AbstractColumn<out C?, T, *>.matches(
    regex: Any,
    flags: Int = 0
): Query = matches("$regex".toPattern(flags))