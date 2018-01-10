package com.wijayaprinting.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

object PlateReceipts : IntIdTable("plate_receipt") {
    val datetime = datetime("datetime")
    // val employee = reference("employee", Employees).nullable()
    val customer = reference("customer", Customers)
    val note = varchar("note", 100).default("")
    val paid = decimal("price", 15, 2).default(ZERO)
    val printed = bool("printed").default(false)
}

class PlateReceipt(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlateReceipt>(PlateReceipts)

    val orders by PlateOrder referrersOn PlateOrders.receipt

    var datetime by PlateReceipts.datetime
    // var employee by Employee optionalReferencedOn PlateReceipts.employee
    var customer by Customer referencedOn PlateReceipts.customer
    var note by PlateReceipts.note
    var paid by PlateReceipts.paid
    var printed by PlateReceipts.printed

    val total: BigDecimal
        get() {
            var sum: BigDecimal = ZERO
            for (order in orders) sum += order.total
            return sum
        }

    val isPaid: Boolean get() = total == paid
}