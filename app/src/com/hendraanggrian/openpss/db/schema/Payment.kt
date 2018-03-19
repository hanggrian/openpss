package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.dbDateTime
import kotlinx.nosql.Id
import kotlinx.nosql.dateTime
import kotlinx.nosql.double
import kotlinx.nosql.mongodb.DocumentSchema
import org.joda.time.DateTime

object Payments : DocumentSchema<Payment>("payment", Payment::class) {
    val value = double("value")
    val dateTime = dateTime("date_time")
}

data class Payment @JvmOverloads constructor(
    val value: Double,
    val dateTime: DateTime = dbDateTime
) : Document<Payments> {

    override lateinit var id: Id<String, Payments>
}