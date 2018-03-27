package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.BasePlate
import com.hendraanggrian.openpss.db.NamedDocument
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import kotlinx.nosql.Id
import kotlinx.nosql.double

object PlatePrices : NamedDocumentSchema<PlatePrice>("plates", PlatePrice::class) {
    val price = double("price")
}

data class PlatePrice(
    override var name: String,
    override var price: Double
) : NamedDocument<PlatePrices>, BasePlate {

    override lateinit var id: Id<String, PlatePrices>

    override fun toString(): String = name

    companion object {
        fun new(
            name: String,
            price: Double = 0.0
        ): PlatePrice = PlatePrice(name, price)
    }
}