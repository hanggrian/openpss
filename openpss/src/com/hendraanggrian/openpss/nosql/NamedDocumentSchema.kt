package com.hendraanggrian.openpss.nosql

import kotlin.reflect.KClass
import kotlinx.nosql.AbstractColumn

/** Mark schema that has `name` value. */
abstract class NamedDocumentSchema<D : Document<*>>(
    schemaName: String,
    klass: KClass<D>
) : Schema<D>(schemaName, klass) {

    abstract val name: AbstractColumn<String, *, String>
}
