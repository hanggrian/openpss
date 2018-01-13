package com.wijayaprinting.nosql

/*
object PlateOrders : IntIdTable("plate_order") {
    val receipt = reference("receipt", PlateReceipts, CASCADE)
    val plate = reference("plate", Plates)
    val qty = integer("qty")
    val price = decimal("price", 15, 2)
}

class PlateOrder(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlateOrder>(PlateOrders)

    var receipt by PlateReceipt referencedOn PlateOrders.receipt
    var plate by Plate referencedOn PlateOrders.plate
    var qty by PlateOrders.qty
    var price by PlateOrders.price

    val total: BigDecimal get() = qty.toBigDecimal() * price
}*/
