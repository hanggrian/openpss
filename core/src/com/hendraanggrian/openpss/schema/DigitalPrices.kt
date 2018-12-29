package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.DigitalPrice
import com.hendraanggrian.openpss.nosql.NamedSchema
import kotlinx.nosql.double
import kotlinx.nosql.string

object DigitalPrices : NamedSchema<DigitalPrice>(DigitalPrices, DigitalPrice::class) {
    override val name = string("name")
    val oneSidePrice = double("one_side_price")
    val twoSidePrice = double("two_side_price")

    override fun toString(): String = "digital_prices"
}