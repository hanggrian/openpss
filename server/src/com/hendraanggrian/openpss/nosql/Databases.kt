package com.hendraanggrian.openpss.nosql

import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.Setting
import com.hendraanggrian.openpss.routing.isEmpty
import com.hendraanggrian.openpss.schema.Customers
import com.hendraanggrian.openpss.schema.DigitalPrices
import com.hendraanggrian.openpss.schema.Employees
import com.hendraanggrian.openpss.schema.Invoices
import com.hendraanggrian.openpss.schema.Logs
import com.hendraanggrian.openpss.schema.OffsetPrices
import com.hendraanggrian.openpss.schema.Payments
import com.hendraanggrian.openpss.schema.PlatePrices
import com.hendraanggrian.openpss.schema.Recesses
import com.hendraanggrian.openpss.schema.Settings
import com.hendraanggrian.openpss.schema.Wages
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.MongoException
import com.mongodb.ServerAddress
import kotlinx.nosql.DocumentSchema
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.util.Date

private lateinit var DATABASE: MongoDB
private val TABLES: Array<DocumentSchema<String, out Document<*>>> = arrayOf(
    Customers,
    DigitalPrices,
    Employees,
    Logs,
    Settings,
    Invoices,
    OffsetPrices,
    Payments,
    PlatePrices,
    Recesses,
    Wages
)

fun startConnection() {
    DATABASE = MongoDB(
        arrayOf(ServerAddress(ServerAddress.defaultHost(), ServerAddress.defaultPort())),
        BuildConfig.DATABASE_NAME,
        arrayOf(
            MongoCredential.createCredential(
                BuildConfig.DATABASE_USER,
                "admin",
                BuildConfig.DATABASE_PASS.toCharArray()
            )
        ),
        MongoClientOptions.Builder().serverSelectionTimeout(3000).build(),
        TABLES
    )
    transaction {
        if (Employees { name.equal(Employee.BACKDOOR.name) }.isEmpty()) {
            Employees += Employee.BACKDOOR
        }
        listOf(Settings.LANGUAGE, Settings.INVOICE_HEADERS)
            .filter { (first, _) -> Settings { key.equal(first) }.isEmpty() }
            .forEach { Settings += Setting(it.first, it.second) }
    }
}

/** Date and time of server. */
fun dbDateTime(): DateTime = DateTime(evalDate)

/** Local date of server. */
fun dbDate(): LocalDate = LocalDate.fromDateFields(evalDate)

/** Local time of server. */
fun dbTime(): LocalTime = LocalTime.fromDateFields(evalDate)

@Suppress("DEPRECATION")
private inline val evalDate: Date
    get() = DATABASE.db.doEval("new Date()").getDate("retval")

/**
 * A failed transaction will most likely throw an exception instance of [MongoException].
 * This function will safely execute a transaction and display an error log on JavaFX if it throws those exceptions.
 *
 * @see [MongoDB.withSession]
 */
@Throws(Exception::class)
fun <T> transaction(statement: (SessionWrapper).() -> T): T =
    DATABASE.withSession { SessionWrapper(this).statement() }