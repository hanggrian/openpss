package com.hendraanggrian.openpss.routing.db

import com.hendraanggrian.openpss.nosql.dbDateTime
import com.hendraanggrian.openpss.nosql.startConnection
import kotlinx.coroutines.runBlocking
import org.apache.log4j.BasicConfigurator
import org.junit.Test

class DatabaseTest {

    @Test
    fun login() {
        runBlocking {
            BasicConfigurator.configure()
            try {
                startConnection()
                println(dbDateTime())
            } catch (e: Exception) {
                e.printStackTrace()
                error(e.message.toString())
            }
        }
    }
}