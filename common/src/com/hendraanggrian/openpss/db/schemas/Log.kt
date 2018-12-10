package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.dateTime
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string
import org.joda.time.DateTime

object Logs : DocumentSchema<Log>("logs", Log::class) {
    val dateTime = dateTime("date_time")
    val message = string("message")
    val employee = string("employee")
}

data class Log(
    val dateTime: DateTime,
    val message: String,
    val employee: String
) : Document<Logs> {

    companion object {

        fun new(
            message: String,
            employee: String
        ): Log = Log(DateTime.now(), message, employee)
    }

    override lateinit var id: Id<String, Logs>
}