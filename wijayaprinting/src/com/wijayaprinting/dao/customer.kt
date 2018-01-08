package com.wijayaprinting.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Customers : IntIdTable("customer") {
    val name = varchar("name", 50).index()
    val since = datetime("since")
    val email = varchar("email", 50)
    val phone1 = varchar("phone1", 15)
    val phone2 = varchar("phone2", 15)
    val note = varchar("note", 255)
}

class Customer(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Customer>(Customers)

    val receipts by Receipt referrersOn Receipts.customer
    val plateReceipts by PlateReceipt referrersOn PlateReceipts.customer

    var name by Customers.name
    var since by Customers.since
    var email by Customers.email
    var phone1 by Customers.phone1
    var phone2 by Customers.phone2
    var note by Customers.note

    override fun toString(): String = "$id. $name"
}