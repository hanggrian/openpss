package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.BaseOffset
import com.hendraanggrian.openpss.db.NamedDocument
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import kotlinx.nosql.Id
import kotlinx.nosql.double
import kotlinx.nosql.integer

object OffsetPrices : NamedDocumentSchema<OffsetPrice>("offset", OffsetPrice::class) {
    val minAmount = integer("min_amount")
    val minPrice = double("min_price")
    val excessPrice = double("excess_price")
}

data class OffsetPrice @JvmOverloads constructor(
    override var name: String,
    override var minQty: Int = 1000,
    override var minPrice: Double = 0.0,
    override var excessPrice: Double = 0.0
) : NamedDocument<OffsetPrices>, BaseOffset {

    override lateinit var id: Id<String, OffsetPrices>

    override fun toString(): String = name
}