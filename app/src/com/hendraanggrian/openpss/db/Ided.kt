package com.hendraanggrian.openpss.db

import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema

/** All DAOs contains ids. */
interface Ided<S : DocumentSchema<*>> {

    var id: Id<String, S>
}