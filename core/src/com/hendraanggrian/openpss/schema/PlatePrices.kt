package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.PlatePrice
import kotlinx.nosql.double
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object PlatePrices : DocumentSchema<PlatePrice>("$PlatePrices", PlatePrice::class),
    NameSchemed {
    override val name = string("name")
    val price = double("price")

    override fun toString(): String = "plate_prices"
}