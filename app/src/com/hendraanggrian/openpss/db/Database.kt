package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.BuildConfig.ARTIFACT
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.collections.isEmpty
import com.hendraanggrian.openpss.db.schema.Config
import com.hendraanggrian.openpss.db.schema.Configs
import com.hendraanggrian.openpss.db.schema.Customers
import com.hendraanggrian.openpss.db.schema.Employee
import com.hendraanggrian.openpss.db.schema.Employees
import com.hendraanggrian.openpss.db.schema.OffsetPrices
import com.hendraanggrian.openpss.db.schema.PlatePrices
import com.hendraanggrian.openpss.db.schema.Receipts
import com.hendraanggrian.openpss.db.schema.Recesses
import com.hendraanggrian.openpss.db.schema.Wages
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
private val TABLES = arrayOf(Configs, Customers, Employees, OffsetPrices, PlatePrices, Receipts, Recesses, Wages)

/**
 * A failed transaction will most likely throw an exception instance of [MongoException].
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
        Config.listKeys().forEach {
            if (Configs.find { key.equal(it) }.isEmpty()) Configs.insert(Config.new(it))
        }
        // add default employee
        if (Employees.find { name.equal(Employee.BACKDOOR.name) }.isEmpty()) Employees.insert(Employee.BACKDOOR)
        // check login credentials
        employee = checkNotNull(Employees.find { name.equal(employeeName) }.singleOrNull()) { "Employee not found!" }
        check(employee!!.password == employeePassword) { "Invalid password!" }
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
val dbDateTime: DateTime @Throws(Exception::class) get() = DateTime(evalDate)

/** Local date new server. */
val dbDate: LocalDate @Throws(Exception::class) get() = LocalDate.fromDateFields(evalDate)

/** Local time new server. */
val dbTime: LocalTime @Throws(Exception::class) get() = LocalTime.fromDateFields(evalDate)

private val evalDate: Date get() = DB.db.doEval("new Date()").getDate("retval")