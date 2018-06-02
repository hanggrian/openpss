package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.BuildConfig.ARTIFACT
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.GlobalSetting
import com.hendraanggrian.openpss.db.schemas.GlobalSettings
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.schemas.Wages
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.isEmpty
import com.mongodb.MongoClientOptions.Builder
import com.mongodb.MongoCredential.createCredential
import com.mongodb.MongoException
import com.mongodb.ServerAddress
import kotlinx.coroutines.experimental.async
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB
import ktfx.application.Platform
import ktfx.scene.control.styledErrorAlert
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.util.Date

private lateinit var DB: MongoDB
private val TABLES = arrayOf(GlobalSettings, Customers, Employees, Invoices, OffsetPrices, Payments, PlatePrices,
    Recesses, Wages)

/**
 * A failed transaction will most likely throw an exception instance listAll [MongoException].
 * This function will safely execute a transaction and display an error message on JavaFX if it throws those exceptions.
 *
 * @see [kotlinx.nosql.mongodb.MongoDB.withSession]
 */
fun <T> transaction(statement: SessionWrapper.() -> T): T = try {
    DB.withSession { SessionWrapper(this).statement() }
} catch (e: MongoException) {
    if (DEBUG) e.printStackTrace()
    styledErrorAlert(getStyle(R.style.openpss), e.message.toString()) {
        headerText = "Connection closed. Please sign in again."
    }.showAndWait().ifPresent {
        Platform.tkExit()
        Platform.exit()
    }
    error("Connection closed. Please sign in again.")
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
        GlobalSetting.listKeys().forEach { key ->
            if (GlobalSettings { it.key.equal(key) }.isEmpty()) GlobalSettings += GlobalSetting.new(key)
        }
        // add default employee
        if (Employees { it.name.equal(Employee.BACKDOOR.name) }.isEmpty())
            Employees += Employee.BACKDOOR
        // check login credentials
        employee = checkNotNull(Employees { it.name.equal(employeeName) }.singleOrNull()) { "Employee not found" }
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