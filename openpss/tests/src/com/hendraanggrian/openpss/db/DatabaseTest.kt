package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.assumeNotInTravis
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.io.properties.LoginFile
import kotlinx.coroutines.runBlocking
import org.apache.log4j.BasicConfigurator
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class DatabaseTest {

    @BeforeTest
    @Throws(Exception::class)
    fun before() = assumeNotInTravis()

    @Test fun login() {
        if (LoginFile.isDbValid()) {
            runBlocking {
                BasicConfigurator.configure()
                try {
                    login(
                        LoginFile.DB_HOST, LoginFile.DB_PORT, LoginFile.DB_USER, LoginFile.DB_PASSWORD,
                        Employee.BACKDOOR.name, Employee.BACKDOOR.password
                    )
                    println(dbDateTime)
                } catch (e: Exception) {
                    e.printStackTrace()
                    error(e.message.toString())
                }
            }
        }
    }
}