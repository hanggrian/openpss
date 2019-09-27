package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Customer
import com.hendraanggrian.openpss.nosql.NamedDocumentSchema
import com.hendraanggrian.openpss.nosql.Schemed
import kotlinx.nosql.ListColumn
import kotlinx.nosql.boolean
import kotlinx.nosql.date
import kotlinx.nosql.integer
import kotlinx.nosql.nullableString
import kotlinx.nosql.string

object Customers : NamedDocumentSchema<Customer>("customers", Customer::class) {
    override val name = string("name")
    val isCompany = boolean("is_company")
    val since = date("since")
    val address = nullableString("address")
    val note = nullableString("note")
    val contacts = Contacts()

    class Contacts : ListColumn<Customer.Contact, Invoices>(Customers.Contacts.schemaName, Customer.Contact::class) {
        val type = string("type")
        val value = integer("value")

        companion object : Schemed {
            override val schemaName: String = "contacts"
        }
    }
}
