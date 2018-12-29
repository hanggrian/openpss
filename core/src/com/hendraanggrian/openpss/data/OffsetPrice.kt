package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.schema.OffsetPrices
import kotlinx.nosql.Id

data class OffsetPrice(
    override var name: String,
    var minQty: Int,
    var minPrice: Double,
    var excessPrice: Double
) : NamedDocument<OffsetPrices> {

    companion object {

        fun new(name: String): OffsetPrice = OffsetPrice(name, 1000, 0.0, 0.0)
    }

    override lateinit var id: Id<String, OffsetPrices>

    override fun toString(): String = name
}