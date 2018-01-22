package com.wijayaprinting.db.dao

import com.wijayaprinting.db.Ided
import com.wijayaprinting.db.schema.Receipts
import com.wijayaprinting.db.schema.Customers
import com.wijayaprinting.db.schema.Employees
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