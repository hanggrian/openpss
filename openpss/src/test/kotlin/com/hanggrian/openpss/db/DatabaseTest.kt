@file:Suppress("ktlint:rulebook:exception-subclass-catching")

package com.hanggrian.openpss.db

import com.hanggrian.openpss.db.schemas.Employee
import com.hanggrian.openpss.io.properties.LoginFile
import kotlinx.coroutines.runBlocking
import org.apache.log4j.BasicConfigurator
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class DatabaseTest {
    @Test
    fun login() {
        if (!LoginFile.isDbValid()) {
            return
        }
        runBlocking {
            BasicConfigurator.configure()
            try {
                login(
                    LoginFile.DB_HOST,
                    LoginFile.DB_PORT,
                    LoginFile.DB_USER,
                    LoginFile.DB_PASSWORD,
                    Employee.BACKDOOR.name,
                    Employee.BACKDOOR.password,
                )
                println(dbDateTime)
            } catch (e: Exception) {
                e.printStackTrace()
                error(e.message.toString())
            }
        }
    }
}
