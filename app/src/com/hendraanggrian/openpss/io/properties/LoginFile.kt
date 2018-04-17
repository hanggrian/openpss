package com.hendraanggrian.openpss.io.properties

import com.mongodb.ServerAddress.defaultPort
import java.util.Locale.US

/** Properties file for login settings that aren't manually configurable by user. */
object LoginFile : PropertiesFile("login") {

    var EMPLOYEE: String by ""
    var LANGUAGE: String by US.language

    var DB_HOST: String by ""
    var DB_PORT: Int by defaultPort()
    var DB_USER: String by ""
    var DB_PASSWORD: String by ""

    fun isDbValid(): Boolean = DB_HOST.isNotBlank() && DB_PORT != 0 && DB_USER.isNotBlank() && DB_PASSWORD.isNotBlank()
}