@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.nosql

import com.mongodb.MongoException
import com.wijayaprinting.BuildConfig.DEBUG
import javafx.application.Platform
import kotfx.errorAlert
import kotlinx.nosql.mongodb.MongoDBSession

/**
 * A failed transaction will most likely throw an exception instance of [MongoException].
 * This function will safely execute a transaction and display an error message on JavaFX if it throws those exceptions.
 *
 * @see [kotlinx.nosql.mongodb.MongoDB.withSession]
 */
inline fun <R> transaction(noinline statement: MongoDBSession.() -> R): R? = try {
    NoSQL.DB.withSession(statement)
} catch (e: MongoException) {
    if (DEBUG) e.printStackTrace()
    errorAlert(e.message.toString()) { headerText = "Connection closed. Please sign in again." }.showAndWait().ifPresent {
        Platform.exit()
        System.exit(0)
    }
    null
}