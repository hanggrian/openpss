package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.nosql.StringId
import com.hendraanggrian.openpss.schema.OffsetPrices

data class OffsetPrice(
    override var name: String,
    var minQty: Int,
    var minPrice: Double,
    var excessPrice: Double
) : NamedDocument<OffsetPrices> {

    companion object {

        fun new(name: String): OffsetPrice = OffsetPrice(name, 1000, 0.0, 0.0)
    }

    override lateinit var id: StringId<OffsetPrices>

    override fun toString(): String = name
}
