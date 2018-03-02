package com.hendraanggrian.openpss.db.dao

import com.hendraanggrian.openpss.db.Ided
import com.hendraanggrian.openpss.db.schema.Customers
import com.hendraanggrian.openpss.db.schema.Employees
import com.hendraanggrian.openpss.db.schema.Receipts
import kotlinx.nosql.Id
import org.joda.time.DateTime

data class Receipt(
    val employeeId: Id<String, Employees>,
    val customerId: Id<String, Customers>,
    val dateTime: DateTime,
    val note: String,
    val paid: Double,
    val printed: Boolean
) : Ided<Receipts> {
    override lateinit var id: Id<String, Receipts>
}