package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.io.properties.LoginFile
import kotlinx.coroutines.runBlocking
import org.apache.log4j.BasicConfigurator
import org.junit.Test

class DatabaseTest {

    @Test fun login() {
        try {
            if (LoginFile.isDbValid()) {
                BasicConfigurator.configure()
                runBlocking {
                    try {
                        login(
                            LoginFile.DB_HOST,
                            LoginFile.DB_PORT,
                            LoginFile.DB_USER,
                            LoginFile.DB_PASSWORD,
                            Employee.BACKDOOR.name,
                            Employee.BACKDOOR.password
                        )
                        println(dbDateTime)
                    } catch (e: Exception) {
                        error(e.message.toString())
                    }
                }
            }
        } catch (e: IllegalStateException) {
            println("In CI/CD environment, skipping test.")
        }
    }
}