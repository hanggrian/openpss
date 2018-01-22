package com.wijayaprinting.db.dao

import com.wijayaprinting.db.Ided
import kotlinx.nosql.Id

data class Wage(
        var wageId: Int,
        var daily: Int,
        var hourlyOvertime: Int
) : Ided<Wages> {
    override lateinit var id: Id<String, Wages>
}