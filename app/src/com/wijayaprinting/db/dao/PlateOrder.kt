package com.wijayaprinting.db.dao

import com.wijayaprinting.db.schema.PlateOrders
import com.wijayaprinting.db.schema.Plates
import kotlinx.nosql.Id

data class PlateOrder(
        var plateId: Id<String, Plates>?,
        var qty: Int,
        var price: Double,
        override var total: Double
) : Order<PlateOrder, PlateOrders>() {
    override lateinit var id: Id<String, PlateOrders>
}