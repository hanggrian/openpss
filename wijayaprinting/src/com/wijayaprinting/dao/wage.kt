package com.wijayaprinting.dao

import com.wijayaprinting.internal.CustomIdTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

object Wages : CustomIdTable<Int>("wage") {
    override val id = integer("id").primaryKey().entityId()
    val daily = integer("daily")
    val hourlyOvertime = integer("hourly_overtime")
}

class Wage(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Wage>(Wages)

    var daily by Wages.daily
    var hourlyOvertime by Wages.hourlyOvertime
}