package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.nosql.Document
import com.hendraanggrian.openpss.nosql.Schema
import com.hendraanggrian.openpss.nosql.StringId
import kotlinx.nosql.dateTime
import kotlinx.nosql.string
import org.joda.time.DateTime

object Logs : Schema<Log>("logs", Log::class) {
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
        ): Log =
            Log(DateTime.now(), message, login)
    }

    override lateinit var id: StringId<Logs>
}
