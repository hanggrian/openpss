package com.hendraanggrian.openpss.db

import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema

/**
 * Base class of all DAOs, referred to as a document of NoSQL database.
 * All documents have unique [id].
 */
abstract class Document<S : DocumentSchema<*>> {

    /** Object ID of a MongoDB document. */
    abstract var id: Id<String, S>
}