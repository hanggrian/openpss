package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.db.schemas.Employees
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

    if (LoginFile.isDbValid()) given("a database") {
        configure()
        runBlocking {
            try {
                login(LoginFile.DB_HOST, LoginFile.DB_PORT, LoginFile.DB_USER, LoginFile.DB_PASSWORD, Employees.BACKDOOR.name, Employees.BACKDOOR.password)
                it("should return correct date") {
                    println(dbDateTime)
                }
            } catch (e: Exception) {
                error(e.message.toString())
            }
        }
    }
})