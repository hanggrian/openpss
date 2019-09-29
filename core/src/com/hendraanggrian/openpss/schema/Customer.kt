package com.hendraanggrian.openpss.schema

import com.google.gson.annotations.SerializedName
import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.nosql.NamedDocumentSchema
import com.hendraanggrian.openpss.nosql.Schemed
import com.hendraanggrian.openpss.nosql.StringId
import kotlinx.nosql.ListColumn
import kotlinx.nosql.boolean
import kotlinx.nosql.date
import kotlinx.nosql.integer
import kotlinx.nosql.nullableString
import kotlinx.nosql.string
import org.joda.time.LocalDate

object Customers : NamedDocumentSchema<Customer>("customers", Customer::class) {
    override val name = string("name")
    val isCompany = boolean("is_company")
    val since = date("since")
    val address = nullableString("address")
    val note = nullableString("note")
    val contacts = Contacts()

    class Contacts : ListColumn<Customer.Contact, Invoices>(schemaName, Customer.Contact::class) {
        val type = string("type")
        val value = integer("value")

        companion object : Schemed {
            override val schemaName: String = "contacts"
        }
    }
}

data class Customer(
    override var name: String,
    @SerializedName("is_company") val isCompany: Boolean,
    val since: LocalDate,
    var address: String?,
    var note: String?,
    var contacts: List<Contact>
) : NamedDocument<Customers> {

    companion object {

        fun new(
            name: String,
            isCompany: Boolean,
            since: LocalDate
        ): Customer =
            Customer(name, isCompany, since, null, null, listOf())
    }

    override lateinit var id: StringId<Customers>

    override fun toString(): String = name

    data class Contact(
        val type: String,
        val value: String
    ) {

        companion object;

        override fun toString(): String = value
    }
}
