package com.hendraanggrian.openpss.io.properties

import javafx.beans.property.StringProperty

/** Properties file for MongoDB connection. */
object MongoFile : PropertiesFile("mongo") {

    override val pairs: Array<Pair<String, String>>
        get() = arrayOf(
            "host" to "",
            "port" to "",
            "user" to "",
            "password" to ""
        )

    val host: StringProperty by this
    val port: StringProperty by this
    val user: StringProperty by this
    val password: StringProperty by this

    fun isValid(): Boolean = host.value.isNotBlank() &&
        port.value.isNotBlank() &&
        user.value.isNotBlank() &&
        password.value.isNotBlank()
}