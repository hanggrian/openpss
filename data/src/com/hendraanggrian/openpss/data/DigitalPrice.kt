package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.schema.DigitalPrices
import kotlinx.nosql.Id
import kotlinx.serialization.Serializable

@Serializable
data class DigitalPrice(
    override var name: String,
    var oneSidePrice: Double,
    var twoSidePrice: Double
) : Document<DigitalPrices>, Named {

    companion object {
        fun new(name: String): DigitalPrice = DigitalPrice(name, 0.0, 0.0)
    }

    override lateinit var id: Id<String, DigitalPrices>

    override fun toString(): String = name
}