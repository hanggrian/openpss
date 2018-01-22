package com.wijayaprinting.db.schema

import com.wijayaprinting.db.NamedDocumentSchema
import com.wijayaprinting.db.dao.Offset
import kotlinx.nosql.double
import kotlinx.nosql.integer

object Offsets : NamedDocumentSchema<Offset>("offset", Offset::class) {
    val minAmount = integer("min_amount")
    val minPrice = double("min_price")
    val excessPrice = double("excess_price")
}