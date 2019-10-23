package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.nosql.NamedDocumentSchema
import com.hendraanggrian.openpss.nosql.StringId
import kotlinx.nosql.double
import kotlinx.nosql.string

object DigitalPrices : NamedDocumentSchema<DigitalPrice>("digital_prices", DigitalPrice::class) {
    override val name = string("name")
    val oneSidePrice = double("one_side_price")
    val twoSidePrice = double("two_side_price")
}

data class DigitalPrice(
    override var name: String,
    var oneSidePrice: Double,
    var twoSidePrice: Double
) : NamedDocument<DigitalPrices> {

    companion object {

        fun new(name: String): DigitalPrice =
            DigitalPrice(name, 0.0, 0.0)
    }

    override lateinit var id: StringId<DigitalPrices>

    override fun toString(): String = name
}
