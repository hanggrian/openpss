package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.SplitPriced
import com.hendraanggrian.openpss.db.NamedDocument
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import kotlinx.nosql.Id
import kotlinx.nosql.double
import kotlinx.nosql.integer

object OffsetPrices : NamedDocumentSchema<OffsetPrice>("offsets", OffsetPrice::class) {
    val minQty = integer("min_qty")
    val minPrice = double("min_price")
    val excessPrice = double("excess_price")
}

data class OffsetPrice(
    override var name: String,
    override var minQty: Int,
    override var minPrice: Double,
    override var excessPrice: Double
) : NamedDocument<OffsetPrices>, SplitPriced {

    override lateinit var id: Id<String, OffsetPrices>

    override fun toString(): String = name

    companion object {
        fun new(name: String): OffsetPrice = OffsetPrice(name, 1000, 0.0, 0.0)
    }
}