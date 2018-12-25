package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.schema.Logs
import kotlinx.nosql.Id
import kotlinx.serialization.Serializable
import org.joda.time.DateTime

@Serializable
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