package com.wijayaprinting.nosql

import kotlinx.nosql.Id
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema

object Wages : DocumentSchema<Wage>("wage", Wage::class) {
    val employeeId = integer("employee_id")
    val daily = integer("daily")
    val hourlyOvertime = integer("hourly_overtime")
}

data class Wage(
        val employeeId: Int,
        val daily: Int,
        val hourlyOvertime: Int
) {
    val id: Id<String, Wages>? = null
}