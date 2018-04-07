package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Priced
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
) : NamedDocument<PlatePrices>, Priced {

    override lateinit var id: Id<String, PlatePrices>

    override fun toString(): String = name

    companion object {
        fun new(name: String): PlatePrice = PlatePrice(name, 0.0)
    }
}