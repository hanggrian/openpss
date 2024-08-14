package com.hanggrian.openpss.db

import com.hanggrian.openpss.BuildConfig
import com.hanggrian.openpss.BuildConfig.ARTIFACT
import com.hanggrian.openpss.OpenPssApp
import com.hanggrian.openpss.STYLESHEET_OPENPSS
import com.hanggrian.openpss.db.schemas.Customers
import com.hanggrian.openpss.db.schemas.DigitalPrices
import com.hanggrian.openpss.db.schemas.Employee
import com.hanggrian.openpss.db.schemas.Employees
import com.hanggrian.openpss.db.schemas.GlobalSettings
import com.hanggrian.openpss.db.schemas.Invoices
import com.hanggrian.openpss.db.schemas.OffsetPrices
import com.hanggrian.openpss.db.schemas.Payments
import com.hanggrian.openpss.db.schemas.PlatePrices
import com.hanggrian.openpss.db.schemas.Recesses
import com.hanggrian.openpss.db.schemas.Wages
import com.mongodb.MongoClientOptions.Builder
import com.mongodb.MongoCredential.createCredential
import com.mongodb.MongoException
import com.mongodb.ServerAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB
import ktfx.dialogs.errorAlert
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

private lateinit var database: MongoDB
private val TABLES =
    arrayOf(
        Customers,
        DigitalPrices,
        Employees,
        GlobalSettings,
        Invoices,
        OffsetPrices,
        Payments,
        PlatePrices,
        Recesses,
        Wages,
    )
private const val TIMEOUT = 10000

/**
 * A failed transaction will most likely throw an exception instance listAll [MongoException].
 * This function will safely execute a transaction and display an error log on JavaFX if it throws
 * those exceptions.
 *
 * @see [kotlinx.nosql.mongodb.MongoDB.withSession]
 */
fun <T> transaction(statement: ExtendedSession.() -> T): T =
    try {
        database.withSession { ExtendedSession(this).statement() }
    } catch (e: MongoException) {
        if (BuildConfig.DEBUG) e.printStackTrace()
        errorAlert(e.message.toString()) {
            dialogPane.stylesheets += STYLESHEET_OPENPSS
            headerText = "Connection closed. Please sign in again."
        }.ifPresent { OpenPssApp.exit() }
        error("Connection closed. Please sign in again.")
    }

@Throws(MongoException::class)
suspend fun login(
    host: String,
    port: Int,
    user: String,
    password: String,
    employeeName: String,
    employeePassword: String,
): Employee {
    lateinit var employee: Employee
    database = connect(host, port, user, password)
    transaction {
        // check first time installation
        TABLES.mapNotNull { it as? Setupable }.forEach { it.setup(this) }
        // check login credentials
        employee =
            checkNotNull(Employees { it.name.equal(employeeName) }.singleOrNull()) {
                "Employee not found"
            }
        check(employee.password == employeePassword) { "Invalid password" }
    }
    employee.clearPassword()
    return employee
}

@Throws(MongoException::class)
private suspend fun connect(host: String, port: Int, user: String, password: String): MongoDB =
    withContext(Dispatchers.Default) {
        MongoDB(
            arrayOf(ServerAddress(host, port)),
            ARTIFACT,
            arrayOf(createCredential(user, "admin", password.toCharArray())),
            Builder().serverSelectionTimeout(TIMEOUT).build(),
            TABLES,
        )
    }

/** Date and time new server. */
val dbDateTime: DateTime get() = DateTime(evalDate)

/** Local date new server. */
val dbDate: LocalDate get() = LocalDate.fromDateFields(evalDate)

/** Local time new server. */
val dbTime: LocalTime get() = LocalTime.fromDateFields(evalDate)

private val evalDate get() = database.db.doEval("new Date()").getDate("retval")
