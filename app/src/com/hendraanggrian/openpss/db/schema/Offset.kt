package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import kotlinx.nosql.Id
import kotlinx.nosql.double
import kotlinx.nosql.integer

object Offsets : NamedDocumentSchema<Offset>("offset", Offset::class) {
    val minAmount = integer("min_amount")
    val minPrice = double("min_price")
    val excessPrice = double("excess_price")
}

data class Offset(
    override var name: String,
    var minAmount: Int,
    var minPrice: Double,
    var excessPrice: Double
) : Named<Offsets> {
    override lateinit var id: Id<String, Offsets>

    companion object {
        const val DEFAULT_AMOUNT = 1000
    }
}