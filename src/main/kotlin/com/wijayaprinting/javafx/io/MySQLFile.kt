package com.wijayaprinting.javafx.io

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class MySQLFile : PropertiesFile("mysql",
        Pair(USERNAME, ""),
        Pair(IP, ""),
        Pair(PORT, "")
) {
    companion object {
        const val USERNAME = "username"
        const val IP = "ip"
        const val PORT = "port"
    }
}