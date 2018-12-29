package com.hendraanggrian.openpss.nosql

import kotlinx.nosql.AbstractColumn
import kotlin.reflect.KClass

/** Mark schema that has `name` value. */
abstract class NamedSchema<D : Any>(schemed: NamedSchemed, valueClass: KClass<D>) : Schema<D>(schemed, valueClass) {

    abstract val name: AbstractColumn<String, *, String>
}