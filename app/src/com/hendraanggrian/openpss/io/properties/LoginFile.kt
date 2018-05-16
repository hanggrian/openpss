package com.hendraanggrian.openpss.io.properties

import com.hendraanggrian.openpss.internationalization.Region
import com.mongodb.ServerAddress.defaultPort

/** Properties file for login settings that aren't manually configurable by user. */
object LoginFile : PropertiesFile("login") {

    var EMPLOYEE: String by ""
    var LANGUAGE: String by Region.US.language
    var COUNTRY: String by Region.US.country

    var DB_HOST: String by ""
    var DB_PORT: Int by defaultPort()
    var DB_USER: String by ""
    var DB_PASSWORD: String by ""

    var region: Region
        get() = Region.from(LANGUAGE, COUNTRY)
        set(value) {
            LANGUAGE = value.language
            COUNTRY = value.country
        }

    fun isDbValid(): Boolean = DB_HOST.isNotBlank() && DB_PORT != 0 && DB_USER.isNotBlank() && DB_PASSWORD.isNotBlank()
}