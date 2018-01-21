package com.wijayaprinting.nosql

import kotlinx.nosql.*
import kotlinx.nosql.mongodb.DocumentSchema
import kotlin.reflect.KClass

open class Orders<D : Any, S : DocumentSchema<D>>(klass: KClass<D>, discriminator: String) : DocumentSchema<D>("order", klass, Discriminator(string("type"), discriminator)) {
    val total = double<S>("total")
}

object PlateOrders : Orders<PlateOrder, PlateOrders>(PlateOrder::class, "plate") {
    val plateId = id("plate_id", Plates)
    val qty = integer("qty")
    val price = double("price")
}

object PrintOrders : Orders<PrintOrder, PrintOrders>(PrintOrder::class, "print")

data class PlateOrder(
        val plateId: Id<String, Plates>,
        val qty: Int,
        val price: Double,
        val total: Double
) {
    lateinit var id: Id<String, PlateOrders>
}

class PrintOrder