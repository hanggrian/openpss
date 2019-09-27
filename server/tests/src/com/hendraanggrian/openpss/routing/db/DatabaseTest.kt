package com.hendraanggrian.openpss.routing.db

import com.hendraanggrian.openpss.nosql.Database
import kotlinx.coroutines.runBlocking
import org.apache.log4j.BasicConfigurator

class DatabaseTest {

    // @Test
    fun login() {
        runBlocking {
            BasicConfigurator.configure()
            try {
                Database.start()
                println(Database.dateTime())
            } catch (e: Exception) {
                e.printStackTrace()
                error(e.message.toString())
            }
        }
    }
}