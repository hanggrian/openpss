package com.hendraanggrian.openpss.db

import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema

/**
 * Base interface of all DAOs, referred to as a document of NoSQL database.
 * All documents have unique [id].
 */
interface Document<S : DocumentSchema<*>> {

    /** Object ID of a MongoDB document. */
    var id: Id<String, S>
}