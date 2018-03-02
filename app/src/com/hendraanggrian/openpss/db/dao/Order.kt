package com.hendraanggrian.openpss.db.dao

import com.hendraanggrian.openpss.db.Ided
import com.hendraanggrian.openpss.db.schema.OffsetOrders
import com.hendraanggrian.openpss.db.schema.PlateOrders
import com.hendraanggrian.openpss.db.schema.Plates
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