package com.wijayaprinting.dao

import com.wijayaprinting.internal.CustomIdTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID

object Employees : CustomIdTable<String>("employee") {
    override val id = varchar("id", 50).primaryKey().entityId()
    val password = varchar("password", 50).default(Employee.DEFAULT_PASSWORD)
    val fullAccess = bool("full_access")
}

class Employee(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Employee>(Employees){
        internal const val DEFAULT_PASSWORD = "1234"
    }

    var password by Employees.password
    val fullAccess by Employees.fullAccess

    val firstTimeLogin: Boolean get() = password == "1234"
}