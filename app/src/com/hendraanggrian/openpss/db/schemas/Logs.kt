package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Logs : DocumentSchema<Log>("logs", Log::class) {
    val employeeId = id("employee_id", Employees)
    val message = string("message")
}

data class Log(
    val employeeId: Id<String, Employees>,
    val message: String
) : Document<Logs> {

    override lateinit var id: Id<String, Logs>
}