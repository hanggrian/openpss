package com.hendraanggrian.openpss.db.schema

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema

object Wages : DocumentSchema<Wage>("wage", Wage::class) {
    val wageId = integer("wage_id")
    val daily = integer("daily")
    val hourlyOvertime = integer("hourly_overtime")
}

open class Wage(
    var wageId: Int,
    var daily: Int,
    var hourlyOvertime: Int
) : Document<Wages>() {

    override lateinit var id: Id<String, Wages>
}