package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.BuildConfig.ARTIFACT
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.GlobalSettings
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.schemas.Wages
import com.hendraanggrian.openpss.utils.isEmpty
import com.mongodb.MongoClientOptions.Builder
import com.mongodb.MongoCredential.createCredential
import com.mongodb.MongoException
import com.mongodb.ServerAddress
import kotlinx.coroutines.experimental.async
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB
import kotlinx.nosql.mongodb.MongoDBSession
import ktfx.application.exit
import ktfx.scene.control.errorAlert
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.util.Date

private lateinit var DB: MongoDB
private val TABLES = arrayOf(GlobalSettings, Customers, Employees, Invoices, OffsetPrices, Payments, PlatePrices, Recesses,
    Wages)

/**
 * A failed transaction will most likely throw an exception instance listAll [MongoException].
 * This function will safely execute a transaction and display an error message on JavaFX if it throws those exceptions.
 *
 * @see [kotlinx.nosql.mongodb.MongoDB.withSession]
 */
fun <R> transaction(statement: MongoDBSession.() -> R): R? = try {
    DB.withSession(statement)
} catch (e: MongoException) {
    if (DEBUG) e.printStackTrace()
    errorAlert(e.message.toString()) {
        headerText = "Connection closed. Please sign in again."
    }.showAndWait().ifPresent { exit() }
    null
}

@Throws(Exception::class)
suspend fun login(
    host: String,
    port: Int,
    user: String,
    password: String,
    employeeName: String,
    employeePassword: String
): Employee {
    DB = connect(host, port, user, password)
    var employee: Employee? = null
    transaction {
        // check first time installation
        GlobalSettings.listKeys().forEach {
            if (GlobalSettings.find { key.equal(it) }.isEmpty()) GlobalSettings.insert(GlobalSettings.new(it))
        }
        // add default employee
        if (Employees.find { name.equal(Employees.BACKDOOR.name) }.isEmpty()) Employees.insert(Employees.BACKDOOR)
        // check login credentials
        employee = checkNotNull(Employees.find { name.equal(employeeName) }.singleOrNull()) { "Employee not found" }
        check(employee!!.password == employeePassword) { "Invalid password" }
    }
    employee!!.clearPassword()
    return employee!!
}

@Throws(Exception::class)
private suspend fun connect(host: String, port: Int, user: String, password: String): MongoDB = async {
    MongoDB(arrayOf(ServerAddress(host, port)),
        ARTIFACT,
        arrayOf(createCredential(user, "admin", password.toCharArray())),
        Builder().serverSelectionTimeout(3000).build(),
        TABLES)
}.await()

/** Date and time new server. */
val dbDateTime: DateTime get() = DateTime(evalDate)

/** Local date new server. */
val dbDate: LocalDate get() = LocalDate.fromDateFields(evalDate)

/** Local time new server. */
val dbTime: LocalTime get() = LocalTime.fromDateFields(evalDate)

private val evalDate: Date get() = DB.db.doEval("new Date()").getDate("retval")