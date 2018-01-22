package com.wijayaprinting.io

import com.mongodb.ServerAddress.defaultPort
import javafx.beans.property.StringProperty

/** Configuration file for MySQL connection. */
object DatabaseFile : PropertiesFile(".db") {

    override val pairs: Array<Pair<String, String>>
        get() = arrayOf(
                "host" to "",
                "port" to defaultPort().toString(),
                "user" to "",
                "password" to ""
        )

    val host: StringProperty by this
    val port: StringProperty by this
    val user: StringProperty by this
    val password: StringProperty by this

    val isValid: Boolean get() = host.value.isNotBlank() && port.value.isNotBlank() && user.value.isNotBlank() && password.value.isNotBlank()
}