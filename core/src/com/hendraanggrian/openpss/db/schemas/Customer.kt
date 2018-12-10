package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Database
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import kotlinx.nosql.Id
import kotlinx.nosql.ListColumn
import kotlinx.nosql.boolean
import kotlinx.nosql.date
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.nullableString
import kotlinx.nosql.string
import org.joda.time.LocalDate

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

data class Customer(
    override var name: String,
    val isCompany: Boolean,
    val since: LocalDate,
    var address: String?,
    var note: String?,
    var contacts: List<Contact>
) : Document<Customers>, Named {

    companion object {

        fun new(
            name: String,
            isCompany: Boolean
        ): Customer = Customer(name, isCompany, Database.date(), null, null, listOf())
    }

    override lateinit var id: Id<String, Customers>

    override fun toString(): String = name

    data class Contact(
        val type: String,
        val value: String
    ) {

        companion object;

        override fun toString(): String = value
    }
}