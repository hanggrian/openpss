package com.hendraanggrian.openpss.server.db

import com.hendraanggrian.openpss.db.SessionWrapper

@Throws(Exception::class)
fun <T> transaction(statement: SessionWrapper.() -> T): T = Database.withSession(statement)