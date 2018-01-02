package com.wijayaprinting.manager.io

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import kotfx.stringConverter
import org.jasypt.util.text.StrongTextEncryptor

/** Configuration file for MySQL connection. */
open class MySQLFile : PropertiesFile(".mysql") {
    companion object : MySQLFile()

    private val encryptor = StrongTextEncryptor().apply { setPassword(computerName) }

    val ip: StringProperty by map
    val port: StringProperty by map
    val user: StringProperty by map
    private val password: StringProperty by map

    val encryptedPassword: StringProperty = SimpleStringProperty().apply { bindBidirectional(password, stringConverter({ encryptor.decrypt(it) }) { encryptor.encrypt(it) }) }

    override val pairs: List<Pair<String, String>>
        get() = listOf(
                "ip" to "",
                "port" to "",
                "user" to "",
                "password" to ""
        )

    private val computerName: String
        get() = System.getenv().let { env ->
            return when {
                env.containsKey("COMPUTERNAME") -> env["COMPUTERNAME"]!!
                env.containsKey("HOSTNAME") -> env["HOSTNAME"]!!
                else -> "Unknown Computer"
            }
        }
}