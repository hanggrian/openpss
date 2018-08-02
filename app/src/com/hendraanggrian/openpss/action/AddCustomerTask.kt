package com.hendraanggrian.openpss.action

import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.dbDate
import com.hendraanggrian.openpss.db.schemas.Customer
import com.hendraanggrian.openpss.db.schemas.Customers

class AddCustomerTask(
    private val name: String
) : Task() {

    override fun SessionWrapper.action() {
        val customer = Customer(name, dbDate, null, null, listOf())
        customer.id = Customers.insert(customer)
    }
}