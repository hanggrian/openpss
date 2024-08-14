package com.hanggrian.openpss.ui.customer

import com.hanggrian.openpss.Action
import com.hanggrian.openpss.Context
import com.hanggrian.openpss.db.ExtendedSession
import com.hanggrian.openpss.db.schemas.Customer
import com.hanggrian.openpss.db.schemas.Customers
import kotlinx.nosql.update

class AddCustomerAction(context: Context, val customer: Customer) : Action<Customer>(context) {
    override fun ExtendedSession.handle(): Customer = customer.also { it.id = Customers.insert(it) }
}

class EditCustomerAction(
    context: Context,
    val customer: Customer,
    val name: String,
    val address: String?,
    val note: String?,
) : Action<Unit>(context, true) {
    override fun ExtendedSession.handle() {
        Customers[customer]
            .projection { name + address + note }
            .update(name, address, note)
    }
}

class AddContactAction(context: Context, val customer: Customer, val contact: Customer.Contact) :
    Action<Unit>(context) {
    override fun ExtendedSession.handle() {
        Customers[customer]
            .projection { contacts }
            .update(customer.contacts + contact)
    }
}

class DeleteContactAction(context: Context, val customer: Customer, val contact: Customer.Contact) :
    Action<Unit>(context, true) {
    override fun ExtendedSession.handle() {
        Customers[customer]
            .projection { contacts }
            .update(customer.contacts - contact)
    }
}
