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

    val hostProperty: StringProperty by map
    var host: String
        get() = hostProperty.get()
        set(value) = hostProperty.set(value)

    val portProperty: StringProperty by map
    var port: String
        get() = portProperty.get()
        set(value) = portProperty.set(value)

    val userProperty: StringProperty by map
    var user: String
        get() = userProperty.get()
        set(value) = userProperty.set(value)

    val passwordProperty: StringProperty by map
    var password: String
        get() = passwordProperty.get()
        set(value) = passwordProperty.set(value)
}