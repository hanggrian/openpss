package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.nosql.NamedDocumentSchema
import com.hendraanggrian.openpss.nosql.StringId
import kotlinx.nosql.double
import kotlinx.nosql.integer
import kotlinx.nosql.string

object OffsetPrices : NamedDocumentSchema<OffsetPrice>("offset_prices", OffsetPrice::class) {
    override val name = string("name")
    val minQty = integer("min_qty")
    val minPrice = double("min_price")
    val excessPrice = double("excess_price")
}

data class OffsetPrice(
    override var name: String,
    var minQty: Int,
    var minPrice: Double,
    var excessPrice: Double
) : NamedDocument<OffsetPrices> {

    companion object {

        fun new(name: String): OffsetPrice =
            OffsetPrice(name, 1000, 0.0, 0.0)
    }

    override lateinit var id: StringId<OffsetPrices>

    override fun toString(): String = name
}
