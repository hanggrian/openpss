package com.hendraanggrian.openpss.schema

import kotlinx.nosql.AbstractColumn

interface NamedSchema {

    val name: AbstractColumn<String, *, String>
}