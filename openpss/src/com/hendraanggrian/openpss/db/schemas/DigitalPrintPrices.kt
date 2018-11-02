package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import kotlinx.nosql.Id
import kotlinx.nosql.double
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object DigitalPrintPrices : DocumentSchema<DigitalPrintPrice>(
    "digital_print_prices",
    DigitalPrintPrice::class
), NamedSchema {
    override val name = string("name")
    val oneSidePrice = double("one_side_price")
    val twoSidePrice = double("two_side_price")
}

data class DigitalPrintPrice(
    override var name: String,
    var oneSidePrice: Double,
    var twoSidePrice: Double
) : Document<DigitalPrintPrices>, Named {

    companion object {
        fun new(name: String): DigitalPrintPrice = DigitalPrintPrice(name, 0.0, 0.0)
    }

    override lateinit var id: Id<String, DigitalPrintPrices>

    override fun toString(): String = name
}