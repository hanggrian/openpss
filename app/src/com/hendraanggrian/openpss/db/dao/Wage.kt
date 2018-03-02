package com.hendraanggrian.openpss.db.dao

import com.hendraanggrian.openpss.db.Ided
import com.hendraanggrian.openpss.db.schema.Wages
import kotlinx.nosql.Id

data class Wage(
    var wageId: Int,
    var daily: Int,
    var hourlyOvertime: Int
) : Ided<Wages> {
    override lateinit var id: Id<String, Wages>
}