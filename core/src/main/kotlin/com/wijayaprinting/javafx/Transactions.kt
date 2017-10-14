@file:JvmName("TransactionsKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx

import kotfx.dialogs.errorAlert
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

/**
 * A failed transaction will most likely throw an exception instance of [SQLException].
 * This function will safely execute a transaction and display an error message on JavaFX if it throws those exceptions.
 *
 * @see [transaction]
 */
inline fun <T> safeTransaction(noinline statement: Transaction.() -> T) {
    try {
        transaction(statement)
    } catch (e: SQLException) {
        errorAlert(e.message ?: "Unknown error!").showAndWait()
    }
}