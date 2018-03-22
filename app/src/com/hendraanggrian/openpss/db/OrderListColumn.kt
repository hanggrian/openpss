package com.hendraanggrian.openpss.db

import kotlinx.nosql.AbstractSchema
import kotlinx.nosql.ListColumn
import kotlinx.nosql.double
import kotlinx.nosql.integer
import kotlinx.nosql.string
import kotlin.reflect.KClass

abstract class OrderListColumn<C : Any, S : AbstractSchema>(
    name: String,
    klass: KClass<C>
) : ListColumn<C, S>(name, klass) {

    val title = string<S>("title")
    val qty = integer<S>("qty")
    val total = double<S>("total")
}