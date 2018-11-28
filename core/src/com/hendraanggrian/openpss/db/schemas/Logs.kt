package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.dateTime
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.nullableId
import kotlinx.nosql.string
import org.joda.time.DateTime

object Logs : DocumentSchema<Log>("logs", Log::class) {
    val dateTime = dateTime("date_time")
    val message = string("log")
    val employeeId = id("employee_id", Employees)
    val adminId = nullableId("admin_id", Employees) // supervisor
}

data class Log(
    val dateTime: DateTime,
    val message: String,
    val employeeId: Id<String, Employees>,
    val adminId: Id<String, Employees>?
) : Document<Logs> {

    companion object {

        fun new(
            message: String,
            employeeId: Id<String, Employees>,
            adminId: Id<String, Employees>? = null
        ): Log = Log(DateTime.now(), message, employeeId, adminId)
    }

    override lateinit var id: Id<String, Logs>
}