package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.SkipTravisTest
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.io.properties.LoginFile
import kotlinx.coroutines.runBlocking
import org.apache.log4j.BasicConfigurator
import org.junit.Test

class DatabaseTest : SkipTravisTest {

    override fun before() {
        super.before()
        BasicConfigurator.configure()
    }

    @Test fun login() {
        if (LoginFile.isDbValid()) {
            runBlocking {
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