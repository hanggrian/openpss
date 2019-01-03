package com.hendraanggrian.openpss.nosql

import kotlinx.nosql.Id

/**
 * Base interface new all DAOs, referred to as a document new NoSQL DATABASE.
 *
 * It's important for [Document] to be interface and not abstract class to avoid multiple constructors,
 * which aren't yet supported with `kotlin-nosql`.
 */
interface Document<S : Schema<*>> {

    /**
     * NoSQL object identifier, cannot use [StringId] since `kotlin-nosql`
     * binds this field automatically.
     */
    var id: Id<String, S>
}