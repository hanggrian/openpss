package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import kotlinx.nosql.Id
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema

object Wages : DocumentSchema<Wage>("wages", Wage::class) {
    val wageId = integer("wage_id")
    val daily = integer("daily")
    val hourlyOvertime = integer("hourly_overtime")
}

data class Wage(
    var wageId: Int,
    var daily: Int,
    var hourlyOvertime: Int
) : Document<Wages> {

    override lateinit var id: Id<String, Wages>
}