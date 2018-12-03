package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Action
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import kotlinx.nosql.update

class AddCustomerAction(component: FxComponent, val customer: Customer) : Action<Customer>(component) {

    override val log: String = getString(R.string._log_customer_add, customer.name)

    override fun SessionWrapper.handle(): Customer = customer.also { it.id = Customers.insert(it) }
}

class EditCustomerAction(
    component: FxComponent,
    val customer: Customer,
    val name: String,
    val address: String?,
    val note: String?
) : Action<Unit>(component, true) {

    override val log: String = getString(R.string._log_customer_edit, customer.name)

    override fun SessionWrapper.handle() {
        Customers[customer]
            .projection { Customers.name + Customers.address + Customers.note }
            .update(name, address, note)
    }
}

class AddContactAction(
    component: FxComponent,
    val customer: Customer,
    val contact: Customer.Contact
) : Action<Unit>(component) {

    override val log: String = getString(R.string._log_contact_add, contact.value, customer.name)

    override fun SessionWrapper.handle() {
        Customers[customer]
            .projection { contacts }
            .update(customer.contacts + contact)
    }
}

class DeleteContactAction(
    component: FxComponent,
    val customer: Customer,
    val contact: Customer.Contact
) : Action<Unit>(component, true) {

    override val log: String = getString(R.string._log_contact_deleted, contact.value, customer.name)

    override fun SessionWrapper.handle() {
        Customers[customer]
            .projection { contacts }
            .update(customer.contacts - contact)
    }
}