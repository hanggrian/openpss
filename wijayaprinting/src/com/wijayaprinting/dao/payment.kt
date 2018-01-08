package com.wijayaprinting.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Payments : IntIdTable("payment") {
    val receipt = reference("receipt", Receipts)
    val datetime = datetime("datetime")
    val value = decimal("value", 15, 2)
    val cash = bool("cash")
    val note = varchar("note", 100)
}

class Payment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Payment>(Payments)

    var receipt by Receipt referencedOn Payments.receipt
    var datetime by Payments.datetime
    var value by Payments.value
    var cash by Payments.cash
    var note by Payments.note
}