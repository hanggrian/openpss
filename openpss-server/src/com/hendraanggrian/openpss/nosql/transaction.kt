package com.hendraanggrian.openpss.nosql

import com.mongodb.MongoException
import kotlinx.nosql.mongodb.MongoDB

/**
 * A failed transaction will most likely throw an exception instance of [MongoException].
 * This function will safely execute a transaction and display an error log on JavaFX if it throws those exceptions.
 *
 * @see [MongoDB.withSession]
 */
@Throws(Exception::class)
fun <T> transaction(statement: (SessionWrapper).() -> T): T =
    Database.DATABASE.withSession { SessionWrapper(this).statement() }
