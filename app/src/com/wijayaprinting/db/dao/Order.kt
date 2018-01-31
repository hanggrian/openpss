package com.wijayaprinting.db.dao

import com.wijayaprinting.db.Ided
import com.wijayaprinting.db.schema.OffsetOrders
import com.wijayaprinting.db.schema.PlateOrders
import com.wijayaprinting.db.schema.Plates
import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema

sealed class Order<D : Any, S : DocumentSchema<D>> : Ided<S> {
    override lateinit var id: Id<String, S>
    open var total: Double = 0.0
}

class OffsetOrder : Order<OffsetOrder, OffsetOrders>()

data class PlateOrder(
        var plateId: Id<String, Plates>?,
        var qty: Int,
        var price: Double,
        override var total: Double
) : Order<PlateOrder, PlateOrders>() {
    override lateinit var id: Id<String, PlateOrders>
}