package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Wage
import com.hendraanggrian.openpss.nosql.Schema
import kotlinx.nosql.integer

object Wages : Schema<Wage>("wages", Wage::class) {
    val wageId = integer("wage_id")
    val daily = integer("daily")
    val hourlyOvertime = integer("hourly_overtime")
}