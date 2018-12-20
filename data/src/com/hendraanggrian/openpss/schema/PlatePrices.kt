package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.PlatePrice
import kotlinx.nosql.double
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object PlatePrices : DocumentSchema<PlatePrice>("plate_prices", PlatePrice::class),
    NamedSchema {
    override val name = string("name")
    val price = double("price")
}