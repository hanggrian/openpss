package com.wijayaprinting.nosql

import kotlinx.nosql.Column
import kotlinx.nosql.Id
import kotlinx.nosql.date
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import org.joda.time.LocalDate

object Customers : DocumentSchema<Customer>("customer", Customer::class) {
    val name = string("name")
    val note = string("note")
    val since = date("since")
    val contact = ContactColumn()

    class ContactColumn : Column<Contact, Customers>("contact", Contact::class) {
        val type = string("type")
        val value = string("value")
    }
}

data class Customer(
        val name: String,
        val note: String,
        val since: LocalDate,
        val contact: Contact
) {
    val id: Id<String, Customers>? = null

    override fun toString(): String = name
}

data class Contact(val type: String, val value: String)