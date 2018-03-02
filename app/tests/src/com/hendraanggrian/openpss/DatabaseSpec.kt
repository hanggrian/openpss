package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.db.Database.login
import com.hendraanggrian.openpss.db.dao.Employee
import com.hendraanggrian.openpss.db.dao.PlateOrder
import com.hendraanggrian.openpss.db.schema.PlateOrders
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.MongoFile
import kotlinx.coroutines.experimental.launch
import org.apache.log4j.BasicConfigurator.configure
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertNotNull

// @RunWith(JUnitPlatform::class)
object DatabaseSpec : Spek({

    if (MongoFile.isValid) given("a database") {
        configure()
        launch {
            try {
                login(MongoFile.host.value, MongoFile.port.value.toInt(), MongoFile.user.value, MongoFile.password.value, Employee.name, Employee.password)
                transaction {
                    val id = PlateOrders.insert(PlateOrder(null, 10, 100.0, 1000.0))
                    it("should return id") {
                        assertNotNull(id)
                    }
                }
            } catch (e: Exception) {
                error(e.message.toString())
            }
        }
    }
})