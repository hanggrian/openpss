package com.hendraanggrian.openpss.db

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