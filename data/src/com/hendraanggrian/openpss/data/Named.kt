package com.hendraanggrian.openpss.data

/** When a schema extends [NamedSchema], its document class must also extend this interface. */
interface Named {

    var name: String
}