package com.hanggrian.openpss.db.schemas

import com.hanggrian.openpss.db.Document
import com.hanggrian.openpss.db.Named
import com.hanggrian.openpss.db.NamedSchema
import kotlinx.nosql.Id
import kotlinx.nosql.double
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object OffsetPrices :
    DocumentSchema<OffsetPrice>(
        "offset_prices",
        OffsetPrice::class,
    ),
    NamedSchema {
    override val name = string("name")
    val minQty = integer("min_qty")
    val minPrice = double("min_price")
    val excessPrice = double("excess_price")
}

data class OffsetPrice(
    override var name: String,
    var minQty: Int,
    var minPrice: Double,
    var excessPrice: Double,
) : Document<OffsetPrices>,
    Named {
    override lateinit var id: Id<String, OffsetPrices>

    override fun toString(): String = name

    companion object {
        fun new(name: String): OffsetPrice = OffsetPrice(name, 1000, 0.0, 0.0)
    }
}
