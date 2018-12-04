package com.hendraanggrian.openpss.server

import com.hendraanggrian.openpss.db.Database
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class App {

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            Database.setup()
            SpringApplication.run(App::class.java, *args)
        }
    }
}