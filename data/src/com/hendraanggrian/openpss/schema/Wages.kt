package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Wage
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema

object Wages : DocumentSchema<Wage>("wages", Wage::class) {
    val wageId = integer("wage_id")
    val daily = integer("daily")
    val hourlyOvertime = integer("hourly_overtime")
}