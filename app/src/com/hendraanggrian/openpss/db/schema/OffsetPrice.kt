package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.BaseOffset
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
) : NamedDocument<OffsetPrices>, BaseOffset {

    override lateinit var id: Id<String, OffsetPrices>

    override fun toString(): String = name

    companion object {
        fun new(
            name: String,
            minQty: Int = 1000,
            minPrice: Double = 0.0,
            excessPrice: Double = 0.0
        ): OffsetPrice = OffsetPrice(name, minQty, minPrice, excessPrice)
    }
}