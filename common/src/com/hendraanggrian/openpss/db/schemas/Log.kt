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
    val login = string("login")
}

data class Log(
    val dateTime: DateTime,
    val message: String,
    val login: String
) : Document<Logs> {

    companion object {

        fun new(
            message: String,
            login: String
        ): Log = Log(DateTime.now(), message, login)
    }

    override lateinit var id: Id<String, Logs>
}