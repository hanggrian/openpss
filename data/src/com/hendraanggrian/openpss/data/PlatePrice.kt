package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.schema.PlatePrices
import kotlinx.nosql.Id

data class PlatePrice(
    override var name: String,
    var price: Double
) : Document<PlatePrices>, Named {

    companion object {
        fun new(name: String): PlatePrice = PlatePrice(name, 0.0)
    }

    override lateinit var id: Id<String, PlatePrices>

    override fun toString(): String = name
}