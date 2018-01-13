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

    private val _host: StringProperty by map
    private val _port: StringProperty by map
    private val _user: StringProperty by map
    private val _password: StringProperty by map

    val hostProperty: StringProperty get() = _host
    var host: String
        get() = hostProperty.get()
        set(value) = hostProperty.set(value)

    val portProperty: StringProperty get() = _port
    var port: String
        get() = portProperty.get()
        set(value) = portProperty.set(value)

    val userProperty: StringProperty get() = _user
    var user: String
        get() = userProperty.get()
        set(value) = userProperty.set(value)

    val passwordProperty: StringProperty get() = _password
    var password: String
        get() = passwordProperty.get()
        set(value) = passwordProperty.set(value)
}