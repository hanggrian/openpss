package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import com.hendraanggrian.openpss.db.dbDate
import com.hendraanggrian.openpss.ui.Listable
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.ui.StringResource
import javafx.collections.ObservableList
import kotlinx.nosql.Id
import kotlinx.nosql.ListColumn
import kotlinx.nosql.date
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import ktfx.collections.observableListOf
import org.joda.time.LocalDate

object Customers : DocumentSchema<Customer>("customers", Customer::class), NamedSchema {
    override val name = string("name")
    val since = date("since")
    val address = string("address")
    val note = string("note")
    val contacts = Contacts()

    fun new(name: String): Customer = Customer(name, dbDate, "", "", listOf())

    class Contacts : ListColumn<Customer.Contact, Invoices>("contacts", Customer.Contact::class) {
        val type = string("type")
        val value = integer("value")

        companion object {
            fun new(
                type: Customer.Contact.Type,
                value: String
            ): Customer.Contact = Customer.Contact(type.toString(), value)
        }
    }
}

data class Customer(
    override var name: String,
    val since: LocalDate,
    val address: String,
    var note: String,
    var contacts: List<Contact>
) : Document<Customers>, Named {

    override lateinit var id: Id<String, Customers>

    override fun toString(): String = name

    data class Contact(
        val type: String,
        val value: String
    ) {

        sealed class Type(resourced: Resourced, id: String) : StringResource(resourced, id) {
            companion object : Listable<Type> {
                override fun listAll(resourced: Resourced): ObservableList<Type> = observableListOf(
                    Phone(resourced),
                    Email(resourced))
            }

            class Phone(resourced: Resourced) : Type(resourced, R.string.phone)
            class Email(resourced: Resourced) : Type(resourced, R.string.email)
        }
    }
}