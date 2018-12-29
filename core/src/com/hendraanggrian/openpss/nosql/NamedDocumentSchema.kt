package com.hendraanggrian.openpss.nosql

import kotlinx.nosql.AbstractColumn
import kotlin.reflect.KClass

/** Mark schema that has `name` value. */
abstract class NamedDocumentSchema<D : Document<*>>(
    schemaName: String,
    klass: KClass<D>
) : Schema<D>(schemaName, klass) {

    abstract val name: AbstractColumn<String, *, String>
}