package com.hanggrian.openpss.db

import kotlinx.nosql.AbstractColumn

interface NamedSchema {
    val name: AbstractColumn<String, *, String>
}
