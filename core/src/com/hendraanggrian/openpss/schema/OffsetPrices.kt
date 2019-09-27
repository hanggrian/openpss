package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.OffsetPrice
import com.hendraanggrian.openpss.nosql.NamedDocumentSchema
import kotlinx.nosql.double
import kotlinx.nosql.integer
import kotlinx.nosql.string

object OffsetPrices : NamedDocumentSchema<OffsetPrice>("offset_prices", OffsetPrice::class) {
    override val name = string("name")
    val minQty = integer("min_qty")
    val minPrice = double("min_price")
    val excessPrice = double("excess_price")
}
