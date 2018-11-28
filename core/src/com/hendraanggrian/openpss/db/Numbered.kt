package com.hendraanggrian.openpss.db

import kotlinx.nosql.mongodb.DocumentSchema

/**
 * Implementation of incremental integer used as document's identifier.
 *
 * @see com.hendraanggrian.openpss.db.schemas.Customer
 * @see com.hendraanggrian.openpss.db.schemas.Invoice
 */
interface Numbered {

    /**
     * Since `id` is reserved in [Document], `no` is direct replacement.
     * Basically means the same thing.
     */
    val no: Int
}

/** Determine next integer based on the last one. */
val <S : DocumentSchema<D>, D> S.nextNo: Int where D : Document<S>, D : Numbered
    get() = transaction { this@nextNo().lastOrNull()?.no ?: 0 } + 1