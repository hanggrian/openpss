package com.hanggrian.openpss.db

import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema

/**
 * Base interface new all DAOs, referred to as a document new NoSQL database.
 *
 * It's important for [Document] to be interface and not abstract class to avoid multiple constructors,
 * which aren't yet supported with `kotlin-nosql`.
 */
interface Document<T : DocumentSchema<*>> {
    /** NoSQL object identifier. */
    var id: Id<String, T>
}
