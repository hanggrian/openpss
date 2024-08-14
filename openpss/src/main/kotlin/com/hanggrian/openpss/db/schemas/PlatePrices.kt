package com.hanggrian.openpss.db.schemas

import com.hanggrian.openpss.db.Document
import com.hanggrian.openpss.db.Named
import com.hanggrian.openpss.db.NamedSchema
import kotlinx.nosql.Id
import kotlinx.nosql.double
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object PlatePrices :
    DocumentSchema<PlatePrice>("plate_prices", PlatePrice::class),
    NamedSchema {
    override val name = string("name")
    val price = double("price")
}

data class PlatePrice(override var name: String, var price: Double) :
    Document<PlatePrices>,
    Named {
    override lateinit var id: Id<String, PlatePrices>

    override fun toString(): String = name

    companion object {
        fun new(name: String): PlatePrice = PlatePrice(name, 0.0)
    }
}
