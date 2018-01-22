package com.wijayaprinting.db

import kotlinx.nosql.*
import kotlinx.nosql.mongodb.DocumentSchema
import kotlin.reflect.KClass

open class Orders<D : Any, S : DocumentSchema<D>>(klass: KClass<D>, discriminator: String) : DocumentSchema<D>("order", klass, Discriminator(string("type"), discriminator)) {
    val total = double<S>("total")
}

sealed class Order<D : Any, S : DocumentSchema<D>> : Ided<S> {
    override lateinit var id: Id<String, S>
    open var total: Double = 0.0
}

object PlateOrders : Orders<PlateOrder, PlateOrders>(PlateOrder::class, "plate") {
    val plateId = id("plate_id", Plates)
    val qty = integer("qty")
    val price = double("price")
}

data class PlateOrder(
        var plateId: Id<String, Plates>?,
        var qty: Int,
        var price: Double,
        override var total: Double
) : Order<PlateOrder, PlateOrders>() {
    override lateinit var id: Id<String, PlateOrders>
}

object PrintOrders : Orders<PrintOrder, PrintOrders>(PrintOrder::class, "print")

class PrintOrder : Order<PrintOrder, PrintOrders>()