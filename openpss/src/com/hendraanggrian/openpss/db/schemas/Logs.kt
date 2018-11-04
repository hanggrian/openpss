package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.dateTime
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import org.joda.time.DateTime

object Logs : DocumentSchema<Log>("logs", Log::class) {
    val dateTime = dateTime("date_time")
    val employeeId = id("employee_id", Employees)
    val message = string("log")
}

data class Log(
    val dateTime: DateTime,
    val employeeId: Id<String, Employees>,
    val message: String
) : Document<Logs> {

    companion object {

        fun new(
            employeeId: Id<String, Employees>,
            message: String
        ): Log = Log(DateTime.now(), employeeId, message)
    }

    override lateinit var id: Id<String, Logs>
}