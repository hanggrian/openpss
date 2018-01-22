package com.wijayaprinting.db.dao

import com.wijayaprinting.db.Named
import com.wijayaprinting.db.schema.Offsets
import kotlinx.nosql.Id

data class Offset(
        override var name: String,
        var minAmount: Int,
        var minPrice: Double,
        var excessPrice: Double
) : Named<Offsets> {
    override lateinit var id: Id<String, Offsets>

    companion object {
        const val DEFAULT_AMOUNT = 1000
    }
}