package com.hendraanggrian.openpss.data

import com.google.gson.annotations.SerializedName
import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.nosql.StringId
import com.hendraanggrian.openpss.schema.Customers
import org.joda.time.LocalDate

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
        ): Customer = Customer(name, isCompany, since, null, null, listOf())
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