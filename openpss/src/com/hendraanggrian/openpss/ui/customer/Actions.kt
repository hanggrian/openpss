package com.hendraanggrian.openpss.ui.customer

import com.hendraanggrian.openpss.content.Action
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers
import kotlinx.nosql.update

class AddCustomerAction(context: Context, name: String) :
    Action<Customer>(context, "Created a customer \'$name\'", {
        Customer.new(name).also { it.id = Customers.insert(it) }
    })

class EditCustomerAction(
    context: Context,
    customer: Customer,
    name: String,
    address: String?,
    note: String?
) : Action<Unit>(context, "Edited customer '$customer'", {
    Customers[customer]
        .projection { Customers.name + Customers.address + Customers.note }
        .update(name, address, note)
})

class AddContactAction(context: Context, customer: Customer, contact: Customer.Contact) :
    Action<Unit>(context, "Added contact '$contact' to '$customer'", {
        Customers[customer]
            .projection { contacts }
            .update(customer.contacts + contact)
    })

class DeleteContactAction(context: Context, customer: Customer, contact: Customer.Contact) :
    Action<Unit>(context, "Deleted contact '$contact' to '$customer'", {
        Customers[customer]
            .projection { contacts }
            .update(customer.contacts - contact)
    })