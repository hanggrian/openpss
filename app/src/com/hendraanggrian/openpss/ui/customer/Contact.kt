package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customer.ContactType
import com.hendraanggrian.openpss.db.schemas.Customer.ContactType.EMAIL
import com.hendraanggrian.openpss.db.schemas.Customer.ContactType.PHONE
import javafx.collections.ObservableList
import ktfx.collections.emptyObservableList
import ktfx.collections.toObservableList

data class Contact(
    val type: ContactType,
    val value: String
) {

    companion object {
        fun listAll(customer: Customer?): ObservableList<Contact> = when (customer) {
            null -> emptyObservableList()
            else -> (customer.phones.map { Contact(PHONE, it) } + customer.emails.map { Contact(EMAIL, it) })
                .toObservableList()
        }
    }
}