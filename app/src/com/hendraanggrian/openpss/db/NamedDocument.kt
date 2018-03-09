package com.hendraanggrian.openpss.db

import kotlinx.nosql.mongodb.DocumentSchema

/** When a schema extends [NamedDocumentSchema], its document class must also extend this class. */
abstract class NamedDocument<S : DocumentSchema<*>> : Document<S>() {

    abstract val name: String

    override fun toString(): String = name
}