package com.wijayaprinting.javafx.io

import javafx.beans.property.SimpleStringProperty

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class LoginFile : PropertiesFile(".wp-login") {

    companion object {
        private const val STAFF = "staff"
        private const val IP = "ip"
        private const val PORT = "port"
        private const val USER = "user"
        private const val PASSWORD = "password"
    }

    val staff = SimpleStringProperty(getString(STAFF, ""))
    val ip = SimpleStringProperty(getString(IP, ""))
    val port = SimpleStringProperty(getString(PORT, ""))
    val user = SimpleStringProperty(getString(USER, ""))
    val password = SimpleStringProperty(getString(PASSWORD, ""))

    override fun save(comments: String?) {
        setString(STAFF, staff.value)
        setString(IP, ip.value)
        setString(PORT, port.value)
        setString(USER, user.value)
        setString(PASSWORD, password.value)
        store(comments)
    }
}