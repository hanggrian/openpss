package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.DigitalPrice
import com.hendraanggrian.openpss.nosql.NamedDocumentSchema
import kotlinx.nosql.double
import kotlinx.nosql.string

object DigitalPrices : NamedDocumentSchema<DigitalPrice>("digital_prices", DigitalPrice::class) {
    override val name = string("name")
    val oneSidePrice = double("one_side_price")
    val twoSidePrice = double("two_side_price")
}