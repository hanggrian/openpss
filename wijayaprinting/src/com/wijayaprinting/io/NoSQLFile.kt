package com.wijayaprinting.io

import javafx.beans.property.StringProperty

/** Configuration file for MySQL connection. */
open class NoSQLFile : PropertiesFile(".nosql",
        "host" to "",
        "port" to "",
        "user" to "",
        "password" to ""
) {
    companion object : NoSQLFile()

    val host: StringProperty by map
    val port: StringProperty by map
    val user: StringProperty by map
    val password: StringProperty by map
}