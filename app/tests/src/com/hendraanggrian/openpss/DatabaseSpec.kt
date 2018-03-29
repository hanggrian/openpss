package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.db.dbDateTime
import com.hendraanggrian.openpss.db.login
import com.hendraanggrian.openpss.db.schema.Employee
import com.hendraanggrian.openpss.io.properties.MongoFile
import kotlinx.coroutines.experimental.runBlocking
import org.apache.log4j.BasicConfigurator.configure
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
object DatabaseSpec : Spek({

    if (MongoFile.isValid()) given("a database") {
        configure()
        runBlocking {
            try {
                login(MongoFile.host.value, MongoFile.port.value.toInt(), MongoFile.user.value, MongoFile.password.value, Employee.BACKDOOR.name, Employee.BACKDOOR.password)
                it("should return correct date") {
                    println(dbDateTime)
                }
            } catch (e: Exception) {
                error(e.message.toString())
            }
        }
    }
})