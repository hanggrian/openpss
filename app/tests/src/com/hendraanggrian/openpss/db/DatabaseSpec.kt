package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.io.properties.LoginFile
import kotlinx.coroutines.experimental.runBlocking
import org.apache.log4j.BasicConfigurator.configure
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
object DatabaseSpec : Spek({

    if (LoginFile.isMongoValid()) given("a database") {
        configure()
        runBlocking {
            try {
                login(LoginFile.host.value, LoginFile.port.value.toInt(), LoginFile.user.value, LoginFile.password.value, Employee.BACKDOOR.name, Employee.BACKDOOR.password)
                it("should return correct date") {
                    println(dbDateTime)
                }
            } catch (e: Exception) {
                error(e.message.toString())
            }
        }
    }
})