package com.wijayaprinting.dao

import com.wijayaprinting.internal.CustomIdTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import java.math.BigDecimal.ZERO

object Plates : CustomIdTable<String>("plate") {
    override val id = varchar("id", 50).primaryKey().entityId()
    val price = decimal("price", 15, 2).default(ZERO)
}

class Plate(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Plate>(Plates)

    var price by Plates.price

    override fun toString(): String = "$id - $price"
}