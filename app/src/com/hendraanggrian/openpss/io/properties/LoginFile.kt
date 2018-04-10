package com.hendraanggrian.openpss.io.properties

import javafx.beans.property.StringProperty
import java.util.Locale.US

/** Properties file for general settings that aren't manually configurable by dbUser. */
object LoginFile : PropertiesFile("login") {

    val EMPLOYEE: StringProperty by null
    val LANGUAGE: StringProperty by US.language

    val DB_HOST: StringProperty by null
    val DB_PORT: StringProperty by null
    val DB_USER: StringProperty by null
    val DB_PASSWORD: StringProperty by null

    fun isDbValid(): Boolean = DB_HOST.value.isNotBlank() &&
        DB_PORT.value.isNotBlank() &&
        DB_USER.value.isNotBlank() &&
        DB_PASSWORD.value.isNotBlank()
}