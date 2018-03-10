package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.NamedDocument
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import kotlinx.nosql.Id
import kotlinx.nosql.double
import kotlinx.nosql.integer

object Offsets : NamedDocumentSchema<Offset>("offset", Offset::class) {
    val minAmount = integer("min_amount")
    val minPrice = double("min_price")
    val excessPrice = double("excess_price")
}

data class Offset @JvmOverloads constructor(
    override var name: String,
    var minAmount: Int = DEFAULT_AMOUNT,
    var minPrice: Double = 0.0,
    var excessPrice: Double = 0.0
) : NamedDocument<Offsets> {

    override lateinit var id: Id<String, Offsets>

    override fun toString(): String = name

    companion object {
        const val DEFAULT_AMOUNT = 1000
    }
}