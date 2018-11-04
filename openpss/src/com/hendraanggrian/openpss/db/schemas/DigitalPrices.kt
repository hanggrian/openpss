package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import kotlinx.nosql.Id
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

data class DigitalPrice(
    override var name: String,
    var oneSidePrice: Double,
    var twoSidePrice: Double
) : Document<DigitalPrices>, Named {

    companion object {
        fun new(name: String): DigitalPrice = DigitalPrice(name, 0.0, 0.0)
    }

    override lateinit var id: Id<String, DigitalPrices>

    override fun toString(): String = name
}