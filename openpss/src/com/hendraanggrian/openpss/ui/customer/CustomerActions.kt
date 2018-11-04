package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Action
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import kotlinx.nosql.update

class AddCustomerAction(context: Context, val name: String) : Action<Customer>(context) {

    override val message: String = getString(R.string._event_customer_add, name)

    override fun SessionWrapper.handle(): Customer = Customer.new(name).also { it.id = Customers.insert(it) }
}

class EditCustomerAction(
    context: Context,
    val customer: Customer,
    val name: String,
    val address: String?,
    val note: String?
) : Action<Unit>(context) {

    override val message: String = getString(R.string._event_customer_edit, customer.name)

    override fun SessionWrapper.handle() {
        Customers[customer]
            .projection { Customers.name + Customers.address + Customers.note }
            .update(name, address, note)
    }
}

class AddContactAction(
    context: Context,
    val customer: Customer,
    val contact: Customer.Contact
) : Action<Unit>(context) {

    override val message: String = getString(R.string._event_contact_add, contact.value, customer.name)

    override fun SessionWrapper.handle() {
        Customers[customer]
            .projection { contacts }
            .update(customer.contacts + contact)
    }
}

class DeleteContactAction(
    context: Context,
    val customer: Customer,
    val contact: Customer.Contact
) : Action<Unit>(context) {

    override val message: String = getString(R.string._event_contact_deleted, contact.value, customer.name)

    override fun SessionWrapper.handle() {
        Customers[customer]
            .projection { contacts }
            .update(customer.contacts - contact)
    }
}