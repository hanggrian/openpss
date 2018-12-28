package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.data.Wage
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema

object Wages : DocumentSchema<Wage>("$Wages", Wage::class), Schemed {
    val wageId = integer("wage_id")
    val daily = integer("daily")
    val hourlyOvertime = integer("hourly_overtime")

    override fun toString(): String = "wages"
}