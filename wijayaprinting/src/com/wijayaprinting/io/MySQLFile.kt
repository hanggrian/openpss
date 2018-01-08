package com.wijayaprinting.io

import javafx.beans.property.StringProperty

/** Configuration file for MySQL connection. */
open class MySQLFile : PropertiesFile(".mysql",
        "ip" to "",
        "port" to "",
        "user" to "",
        "password" to ""
) {
    companion object : MySQLFile()

    val ip: StringProperty by map
    val port: StringProperty by map
    val user: StringProperty by map
    val password: StringProperty by map
}