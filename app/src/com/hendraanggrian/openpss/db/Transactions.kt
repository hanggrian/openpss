package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.mongodb.MongoException
import kotlinfx.application.exit
import kotlinfx.scene.control.errorAlert
import kotlinx.nosql.mongodb.MongoDBSession

/**
 * A failed transaction will most likely throw an exception instance of [MongoException].
 * This function will safely execute a transaction and display an error message on JavaFX if it throws those exceptions.
 *
 * @see [kotlinx.nosql.mongodb.MongoDB.withSession]
 */
fun <R> transaction(statement: MongoDBSession.() -> R): R? = try {
    Database.INSTANCE.withSession(statement)
} catch (e: MongoException) {
    if (DEBUG) e.printStackTrace()
    errorAlert(e.message.toString()) { headerText = "Connection closed. Please sign in again." }.showAndWait().ifPresent { exit() }
    null
}