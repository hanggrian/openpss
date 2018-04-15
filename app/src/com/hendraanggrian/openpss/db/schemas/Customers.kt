package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.NamedDocument
import com.hendraanggrian.openpss.db.NamedDocumentSchema
import com.hendraanggrian.openpss.db.dbDate
import com.hendraanggrian.openpss.ui.Resourced
import kotlinx.nosql.Id
import kotlinx.nosql.date
import kotlinx.nosql.listOfString
import kotlinx.nosql.string
import org.joda.time.LocalDate

object Customers : NamedDocumentSchema<Customer>("customers", Customer::class) {
    val since = date("since")
    val address = string("address")
    val note = string("note")
    val phones = listOfString("phones")
    val emails = listOfString("emails")
}

data class Customer(
    override var name: String,
    val since: LocalDate,
    val address: String,
    var note: String,
    var phones: List<String>,
    var emails: List<String>
) : NamedDocument<Customers> {

    companion object {
        fun new(name: String): Customer = Customer(name, dbDate, "", "", listOf(), listOf())
    }

    override lateinit var id: Id<String, Customers>

    override fun toString(): String = name

    enum class ContactType {
        PHONE, EMAIL;

        fun asString(resourced: Resourced): String = resourced.getString(when (this) {
            PHONE -> R.string.phone
            EMAIL -> R.string.email
        })
    }
}