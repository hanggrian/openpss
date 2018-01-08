package com.wijayaprinting.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption.SET_NULL
import java.math.BigDecimal

object PlateOrders : IntIdTable("plate_order") {
    val receipt = reference("receipt", PlateReceipts)
    val plate = reference("plate", Plates, SET_NULL).nullable()
    val qty = integer("qty")
    val price = decimal("price", 15, 2)
}

class PlateOrder(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlateOrder>(PlateOrders)

    var receipt by Receipt referencedOn PlateOrders.receipt
    var plate by Plate optionalReferencedOn PlateOrders.plate
    var qty by PlateOrders.qty
    var price by PlateOrders.price

    val total: BigDecimal get() = qty.toBigDecimal() * price
}