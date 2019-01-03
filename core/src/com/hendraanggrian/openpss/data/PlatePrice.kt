package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.nosql.StringId
import com.hendraanggrian.openpss.schema.PlatePrices

data class PlatePrice(
    override var name: String,
    var price: Double
) : NamedDocument<PlatePrices> {

    companion object {

        fun new(name: String): PlatePrice = PlatePrice(name, 0.0)
    }

    override lateinit var id: StringId<PlatePrices>

    override fun toString(): String = name
}