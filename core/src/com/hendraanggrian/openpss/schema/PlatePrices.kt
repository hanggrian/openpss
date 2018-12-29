package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.PlatePrice
import com.hendraanggrian.openpss.nosql.NamedSchema
import kotlinx.nosql.double
import kotlinx.nosql.string

object PlatePrices : NamedSchema<PlatePrice>("$PlatePrices", PlatePrice::class) {
    override val name = string("name")
    val price = double("price")

    override fun toString(): String = "plate_prices"
}