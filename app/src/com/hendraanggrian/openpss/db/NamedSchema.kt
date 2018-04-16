package com.hendraanggrian.openpss.db

import kotlinx.nosql.AbstractColumn

interface NamedSchema {

    val name: AbstractColumn<String, *, String>
}