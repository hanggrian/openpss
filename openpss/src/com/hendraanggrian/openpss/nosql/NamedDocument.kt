package com.hendraanggrian.openpss.nosql

/** When a schema extends [NamedDocumentSchema], its document class must also extend this interface. */
interface NamedDocument<S : Schema<*>> : Document<S> {

    var name: String
}
