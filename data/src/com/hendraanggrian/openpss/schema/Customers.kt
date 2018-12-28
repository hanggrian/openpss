package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Customer
import kotlinx.nosql.ListColumn
import kotlinx.nosql.boolean
import kotlinx.nosql.date
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.nullableString
import kotlinx.nosql.string

object Customers : DocumentSchema<Customer>("customers", Customer::class), NamedSchema {
    override val name = string("name")
    val isCompany = boolean("is_company")
    val since = date("since")
    val address = nullableString("address")
    val note = nullableString("note")
    val contacts = Contacts()

    class Contacts : ListColumn<Customer.Contact, Invoices>("contacts", Customer.Contact::class) {
        val type = string("type")
        val value = integer("value")
    }
}