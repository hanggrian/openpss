package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.DigitalPrice
import kotlinx.nosql.double
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object DigitalPrices : DocumentSchema<DigitalPrice>(
    "digital_prices",
    DigitalPrice::class
), NamedSchema {
    override val name = string("name")
    val oneSidePrice = double("one_side_price")
    val twoSidePrice = double("two_side_price")
}