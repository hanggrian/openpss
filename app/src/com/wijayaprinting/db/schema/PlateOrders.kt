package com.wijayaprinting.db.schema

import com.wijayaprinting.db.dao.PlateOrder
import kotlinx.nosql.double
import kotlinx.nosql.id
import kotlinx.nosql.integer

object PlateOrders : Orders<PlateOrder, PlateOrders>(PlateOrder::class, "plate") {
    val plateId = id("plate_id", Plates)
    val qty = integer("qty")
    val price = double("price")
}