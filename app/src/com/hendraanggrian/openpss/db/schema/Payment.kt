package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.dbDateTime
import com.hendraanggrian.openpss.ui.DateTimed
import kotlinx.nosql.Id
import kotlinx.nosql.dateTime
import kotlinx.nosql.double
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import org.joda.time.DateTime

object Payments : DocumentSchema<Payment>("payments", Payment::class) {
    val employeeId = id("employee_id", Employees)
    val dateTime = dateTime("date_time")
    val value = double("value")
}

data class Payment(
    var employeeId: Id<String, Employees>,
    override val dateTime: DateTime,
    var value: Double
) : Document<Payments>, DateTimed {

    override lateinit var id: Id<String, Payments>

    companion object {
        fun new(
            employeeId: Id<String, Employees>,
            value: Double
        ): Payment = Payment(employeeId, dbDateTime, value)
    }
}