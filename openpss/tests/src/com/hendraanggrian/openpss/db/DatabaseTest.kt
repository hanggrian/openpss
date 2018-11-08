package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.ConditionalIgnoreRule
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.io.properties.LoginFile
import kotlinx.coroutines.runBlocking
import org.apache.log4j.BasicConfigurator
import org.junit.Rule
import org.junit.Test

class DatabaseTest {

    @Rule var rule = ConditionalIgnoreRule()

    @Test
    @ConditionalIgnoreRule.ConditionalIgnore(NotRunningInTravis::class)
    fun login() {
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
    }

    class NotRunningInTravis : ConditionalIgnoreRule.IgnoreCondition {
        override fun isSatisfied(): Boolean = System.getProperty("user.name") != "travis"
    }
}