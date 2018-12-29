package com.hendraanggrian.openpss.route.db

import com.hendraanggrian.openpss.nosql.Database
import kotlinx.coroutines.runBlocking
import org.apache.log4j.BasicConfigurator
import org.junit.Test

class DatabaseTest {

    @Test fun login() {
        runBlocking {
            BasicConfigurator.configure()
            try {
                Database.connect()
                println(Database.dateTime())
            } catch (e: Exception) {
                e.printStackTrace()
                error(e.message.toString())
            }
        }
    }
}