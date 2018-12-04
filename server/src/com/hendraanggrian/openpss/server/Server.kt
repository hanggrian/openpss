package com.hendraanggrian.openpss.server

import com.hendraanggrian.openpss.db.Database
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    Database.setup()
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}