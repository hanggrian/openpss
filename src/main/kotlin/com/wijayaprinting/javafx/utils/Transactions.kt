@file:JvmName("TransactionsKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx.utils

import kotfx.dialogs.errorAlert
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

/**
 * A failed transaction will most likely throw an exception instance of [SQLException].
 * This function will safely execute a transaction and display an error message on JavaFX if it throws those exceptions.
 *
 * @return true if a transaction is successful without an error, false otherwise.
 * @see [transaction]
 */
inline fun <T> safeTransaction(noinline statement: Transaction.() -> T): Boolean = try {
    transaction(statement)
    true
} catch (e: SQLException) {
    errorAlert(e.message ?: "Unknown error!").showAndWait()
    false
}