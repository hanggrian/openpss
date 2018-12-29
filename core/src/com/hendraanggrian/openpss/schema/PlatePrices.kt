package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.PlatePrice
import com.hendraanggrian.openpss.nosql.NamedDocumentSchema
import kotlinx.nosql.double
import kotlinx.nosql.string

object PlatePrices : NamedDocumentSchema<PlatePrice>("plate_prices", PlatePrice::class) {
    override val name = string("name")
    val price = double("price")
}