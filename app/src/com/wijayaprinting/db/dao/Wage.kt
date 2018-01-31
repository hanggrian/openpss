package com.wijayaprinting.db.dao

import com.wijayaprinting.db.Ided
import com.wijayaprinting.db.schema.Wages
import kotlinx.nosql.Id

data class Wage(
        var wageId: Int,
        var daily: Int,
        var hourlyOvertime: Int
) : Ided<Wages> {
    override lateinit var id: Id<String, Wages>
}