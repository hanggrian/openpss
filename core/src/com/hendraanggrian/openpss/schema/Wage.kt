package com.hendraanggrian.openpss.schema

import com.hendraanggrian.openpss.nosql.Document
import com.hendraanggrian.openpss.nosql.Schema
import com.hendraanggrian.openpss.nosql.StringId
import kotlinx.nosql.integer

object Wages : Schema<Wage>("wages", Wage::class) {
    val wageId = integer("wage_id")
    val daily = integer("daily")
    val hourlyOvertime = integer("hourly_overtime")
}

data class Wage(
    var wageId: Int,
    var daily: Int,
    var hourlyOvertime: Int
) : Document<Wages> {

    override lateinit var id: StringId<Wages>

    companion object {
        val NOT_FOUND: Wage =
            Wage(0, 0, 0)
    }
}
