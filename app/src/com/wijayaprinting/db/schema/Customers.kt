package com.wijayaprinting.db.schema

import com.wijayaprinting.db.NamedDocumentSchema
import com.wijayaprinting.db.dao.Customer
import kotlinx.nosql.ListColumn
import kotlinx.nosql.date
import kotlinx.nosql.string

object Customers : NamedDocumentSchema<Customer>("customer", Customer::class) {
    val note = string("note")
    val since = date("since")
    val contacts = ContactColumn()

    class ContactColumn : ListColumn<Customer.Contact, Customers>("contacts", Customer.Contact::class) {
        val type = string("type")
        val value = string("value")
    }
}