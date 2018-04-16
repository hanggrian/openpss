package com.hendraanggrian.openpss.db

import kotlinx.nosql.AbstractColumn

interface NamedSchema {

    val name: AbstractColumn<String, *, String>
}

/** When a schema extends [NamedSchema], its document class must also extend this interface. */
interface Named {

    var name: String
}