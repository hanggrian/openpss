package com.hendraanggrian.openpss.schema

import kotlinx.nosql.AbstractColumn

/** Mark schema that has `name` value. */
interface NameSchemed : Schemed {

    val name: AbstractColumn<String, *, String>
}