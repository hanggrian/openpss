package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.nosql.NamedDocumentSchema
import com.hendraanggrian.openpss.nosql.StringId
import kotlinx.nosql.double
import kotlinx.nosql.string

object PlatePrices : NamedDocumentSchema<PlatePrice>("plate_prices", PlatePrice::class) {
    override val name = string("name")
    val price = double("price")
}

data class PlatePrice(
    override var name: String,
    var price: Double
) : NamedDocument<PlatePrices> {

    companion object {

        fun new(name: String): PlatePrice =
            PlatePrice(name, 0.0)
    }

    override lateinit var id: StringId<PlatePrices>

    override fun toString(): String = name
}
