package com.wijayaprinting.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

object Receipts : IntIdTable("receipt") {
    val customer = reference("customer", Customers)
    val datetime = datetime("datetime")
    val value = decimal("value", 15, 2)
    val note = varchar("note", 100)
}

class Receipt(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Receipt>(Receipts)

    val payments by Payment referrersOn Payments.receipt

    var customer by Customer referencedOn Receipts.customer
    var datetime by Receipts.datetime
    var value by Receipts.value
    var note by Receipts.note

    val due: BigDecimal
        get() {
            var paid = ZERO
            payments.forEach { paid += it.value }
            return value - paid
        }

    val isPaid: Boolean get() = due == ZERO
}