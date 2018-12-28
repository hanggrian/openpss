package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.DigitalPrice
import kotlinx.nosql.double
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object DigitalPrices : DocumentSchema<DigitalPrice>("$DigitalPrices", DigitalPrice::class),
    NameSchemed {
    override val name = string("name")
    val oneSidePrice = double("one_side_price")
    val twoSidePrice = double("two_side_price")

    override fun toString(): String = "digital_prices"
}