package com.wijayaprinting.manager

import com.wijayaprinting.db.Database.login
import com.wijayaprinting.db.dao.Employee
import com.wijayaprinting.db.dao.PlateOrder
import com.wijayaprinting.db.schema.PlateOrders
import com.wijayaprinting.db.transaction
import com.wijayaprinting.io.properties.MongoFile
import org.apache.log4j.BasicConfigurator.configure
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertNotNull

// @RunWith(JUnitPlatform::class)
object DatabaseSpec : Spek({

    if (MongoFile.isValid) given("a database") {
        configure()
        login(MongoFile.host.value, MongoFile.port.value.toInt(), MongoFile.user.value, MongoFile.password.value, Employee.name, Employee.password).subscribe({
            transaction {
                val id = PlateOrders.insert(PlateOrder(null, 10, 100.0, 1000.0))
                it("should return id") {
                    assertNotNull(id)
                }
            }
        }) { error(it.message.toString()) }
    }
})