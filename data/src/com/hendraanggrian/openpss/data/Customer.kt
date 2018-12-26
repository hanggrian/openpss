package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.schema.Customers
import kotlinx.nosql.Id
import org.joda.time.LocalDate

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
            isCompany: Boolean,
            since: LocalDate
        ): Customer =
            Customer(name, isCompany, since, null, null, listOf())
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