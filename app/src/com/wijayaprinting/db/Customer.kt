package com.wijayaprinting.db

import javafx.collections.ObservableList
import kotfx.observableListOf
import kotlinx.nosql.Id
import kotlinx.nosql.ListColumn
import kotlinx.nosql.date
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now

object Customers : DocumentSchema<Customer>("customer", Customer::class) {
    val name = string("name")
    val note = string("note")
    val since = date("since")
    val contacts = ContactColumn()

    class ContactColumn : ListColumn<Customer.Contact, Customers>("contacts", Customer.Contact::class) {
        val type = string("type")
        val value = string("value")
    }
}

data class Customer @JvmOverloads constructor(
        var name: String,
        var note: String = "",
        var since: LocalDate = now(),
        var contacts: List<Contact> = listOf()
) {
    lateinit var id: Id<String, Customers>

    override fun toString(): String = name

    data class Contact(
            var type: String,
            var value: String
    )

    companion object {
        private const val TYPE_EMAIL = "email"
        private const val TYPE_PHONE = "phone"

        fun listAllTypes(): ObservableList<String> = observableListOf(TYPE_EMAIL, TYPE_PHONE)
    }
}