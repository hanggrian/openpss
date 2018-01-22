package com.wijayaprinting.db

import kotlinx.nosql.Id
import kotlinx.nosql.double
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Offsets : DocumentSchema<Offset>("offset", Offset::class), NamedColumn<Offsets> {
    override val name = string("name")
    val minAmount = integer("min_amount")
    val minPrice = double("min_price")
    val excessPrice = double("excess_price")
}

data class Offset(
        override var name: String,
        var minAmount: Int,
        var minPrice: Double,
        var excessPrice: Double
) : Named, Ided<Offsets> {
    override lateinit var id: Id<String, Offsets>

    companion object {
        const val DEFAULT_AMOUNT = 1000
    }
}