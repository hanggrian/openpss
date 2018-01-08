package com.wijayaprinting.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

object PlateReceipts : IntIdTable("plate_receipt") {
    val customer = reference("customer", Customers)
    val employee = reference("employee", Employees).nullable()
    val datetime = datetime("datetime")
    val note = varchar("note", 100)
    val paid = decimal("price", 15, 2)
}

class PlateReceipt(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlateReceipt>(PlateReceipts)

    val orders by PlateOrder referrersOn PlateOrders.receipt

    var customer by Customer referencedOn PlateReceipts.customer
    var employee by Employee optionalReferencedOn PlateReceipts.employee
    var datetime by PlateReceipts.datetime
    var note by PlateReceipts.note
    val paid by PlateReceipts.paid

    val total: BigDecimal
        get() {
            var sum: BigDecimal = ZERO
            for (order in orders) sum += order.total
            return sum
        }

    val isPaid: Boolean get() = total == paid
}