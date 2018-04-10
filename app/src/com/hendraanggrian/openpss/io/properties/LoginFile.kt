package com.hendraanggrian.openpss.io.properties

import javafx.beans.property.StringProperty
import java.util.Locale.US

/** Properties file for general settings that aren't manually configurable by user. */
object LoginFile : PropertiesFile("login") {

    override val pairs: Array<Pair<String, Any>>
        get() = arrayOf(
            "employee" to "",
            "language" to US.language,

            "host" to "",
            "port" to "",
            "user" to "",
            "password" to ""
        )

    val employee: StringProperty by this
    val language: StringProperty by this

    val host: StringProperty by this
    val port: StringProperty by this
    val user: StringProperty by this
    val password: StringProperty by this

    fun isMongoValid(): Boolean = host.value.isNotBlank() &&
        port.value.isNotBlank() &&
        user.value.isNotBlank() &&
        password.value.isNotBlank()
}