package com.hendraanggrian.openpss.server.db

import com.hendraanggrian.openpss.server.Database
import kotlinx.coroutines.runBlocking
import org.apache.log4j.BasicConfigurator
import org.junit.Test

class DatabaseTest {

    @Test fun login() {
        runBlocking {
            BasicConfigurator.configure()
            try {
                Database.setup()
                println(Database.dateTime())
            } catch (e: Exception) {
                e.printStackTrace()
                error(e.message.toString())
            }
        }
    }
}