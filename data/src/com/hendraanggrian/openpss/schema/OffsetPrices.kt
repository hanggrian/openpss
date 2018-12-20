package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.OffsetPrice
import kotlinx.nosql.double
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object OffsetPrices : DocumentSchema<OffsetPrice>("offset_prices", OffsetPrice::class),
    NamedSchema {
    override val name = string("name")
    val minQty = integer("min_qty")
    val minPrice = double("min_price")
    val excessPrice = double("excess_price")
}