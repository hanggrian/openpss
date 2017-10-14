package com.wijayaprinting.javafx.io

class MySQLFile : WPFile("mysql",
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