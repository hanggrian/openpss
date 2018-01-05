@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.manager.utils

import javafx.application.Platform
import kotfx.errorAlert
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

/**
 * A failed transaction will most likely throw an exception instance of [SQLException].
 * This function will safely execute a transaction and display an error message on JavaFX if it throws those exceptions.
 *
 * @see [transaction]
 */
inline fun <T> safeTransaction(noinline statement: Transaction.() -> T): T? = try {
    transaction(statement)
} catch (e: SQLException) {
    errorAlert(e.message.toString()) {
        headerText = "The app just crashed."
    }.showAndWait().ifPresent {
        Platform.exit()
        System.exit(0)
    }
    null
}