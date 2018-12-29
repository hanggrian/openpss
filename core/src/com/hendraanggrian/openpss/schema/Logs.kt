package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Log
import com.hendraanggrian.openpss.nosql.Schema
import kotlinx.nosql.dateTime
import kotlinx.nosql.string

object Logs : Schema<Log>("logs", Log::class) {
    val dateTime = dateTime("date_time")
    val message = string("message")
    val login = string("login")
}