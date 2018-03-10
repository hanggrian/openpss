package com.hendraanggrian.openpss.db

import kotlinx.nosql.mongodb.DocumentSchema

/** When a schema extends [NamedDocumentSchema], its document class must also extend this interface. */
interface NamedDocument<S : DocumentSchema<*>> : Document<S> {

    val name: String
}