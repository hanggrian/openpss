package com.hanggrian.openpss.db.schemas

import com.hanggrian.openpss.R
import com.hanggrian.openpss.Resources
import com.hanggrian.openpss.db.Document
import com.hanggrian.openpss.db.Named
import com.hanggrian.openpss.db.NamedSchema
import com.hanggrian.openpss.db.Numbered
import com.hanggrian.openpss.db.dbDate
import com.hanggrian.openpss.util.enumValueOfId
import com.hanggrian.openpss.util.id
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
    val no = integer("no")
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
    override val no: Int,
    override var name: String,
    val isCompany: Boolean,
    val since: LocalDate,
    var address: String?,
    var note: String?,
    var contacts: List<Contact>,
) : Document<Customers>,
    Numbered,
    Named {
    override lateinit var id: Id<String, Customers>

    override fun toString(): String = name

    companion object {
        fun new(name: String, isCompany: Boolean): Customer =
            Customer(Numbered.next(Customers), name, isCompany, dbDate, null, null, listOf())
    }

    data class Contact(val type: String, val value: String) {
        val typedType: Type get() = enumValueOfId(type)

        override fun toString(): String = value

        companion object {
            fun new(type: Type, value: String): Contact = Contact(type.id, value)
        }

        enum class Type : Resources.Enum {
            PHONE {
                override val resourceId: String = R.string_phone
            },
            EMAIL {
                override val resourceId: String = R.string_email
            },
        }
    }
}
