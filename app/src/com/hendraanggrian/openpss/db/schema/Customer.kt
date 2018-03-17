package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.NamedDocument
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import com.hendraanggrian.openpss.db.dbDate
import javafx.collections.ObservableList
import ktfx.collections.observableListOf
import kotlinx.nosql.Id
import kotlinx.nosql.ListColumn
import kotlinx.nosql.date
import kotlinx.nosql.string
import org.joda.time.LocalDate

object Customers : NamedDocumentSchema<Customer>("customer", Customer::class) {
    val note = string("note")
    val since = date("since")
    val contacts = ContactColumn()

    class ContactColumn : ListColumn<Customer.Contact, Customers>("contacts", Customer.Contact::class) {
        val type = string("type")
        val value = string("value")
    }
}

data class Customer @JvmOverloads constructor(
    override val name: String,
    var note: String = "",
    var since: LocalDate = dbDate,
    var contacts: List<Contact> = listOf()
) : NamedDocument<Customers> {

    override lateinit var id: Id<String, Customers>

    override fun toString(): String = name

    data class Contact(
        var type: String,
        var value: String
    ) {
        companion object {
            private const val TYPE_EMAIL = "email"
            private const val TYPE_PHONE = "phone"

            fun listTypes(): ObservableList<String> = observableListOf(TYPE_EMAIL, TYPE_PHONE)
        }
    }
}