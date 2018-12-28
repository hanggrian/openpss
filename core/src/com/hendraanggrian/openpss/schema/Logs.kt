package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Log
import kotlinx.nosql.dateTime
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Logs : DocumentSchema<Log>("$Logs", Log::class), Schemed {
    val dateTime = dateTime("date_time")
    val message = string("message")
    val login = string("login")

    override fun toString(): String = "logs"
}