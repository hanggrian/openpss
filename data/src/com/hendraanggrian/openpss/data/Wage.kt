package com.hendraanggrian.openpss.data

import com.hendraanggrian.openpss.schema.Wages
import kotlinx.nosql.Id
import kotlinx.serialization.Serializable

@Serializable
data class Wage(
    var wageId: Int,
    var daily: Int,
    var hourlyOvertime: Int
) : Document<Wages> {

    override lateinit var id: Id<String, Wages>
}