package com.wijayaprinting.manager.io

import javafx.beans.property.StringProperty

/** Configuration file for MySQL connection. */
open class MySQLFile : PropertiesFile(".mysql") {
    companion object : MySQLFile()

    val ip: StringProperty by map
    val port: StringProperty by map
    val user: StringProperty by map
    val password: StringProperty by map

    override val pairs: List<Pair<String, String>>
        get() = listOf(
                "ip" to "",
                "port" to "",
                "user" to "",
                "password" to ""
        )
}