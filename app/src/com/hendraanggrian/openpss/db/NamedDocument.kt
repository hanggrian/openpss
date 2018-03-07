package com.hendraanggrian.openpss.db

import kotlinx.nosql.mongodb.DocumentSchema

/** Some DAOs have name, in which case [toString] must return the name. */
interface NamedDocument<S : DocumentSchema<*>> : Document<S> {

    val name: String

    override fun toString(): String
}