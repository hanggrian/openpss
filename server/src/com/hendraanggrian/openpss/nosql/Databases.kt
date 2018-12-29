package com.hendraanggrian.openpss.nosql

import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.GlobalSetting
import com.hendraanggrian.openpss.route.isEmpty
import com.hendraanggrian.openpss.schema.Customers
import com.hendraanggrian.openpss.schema.DigitalPrices
import com.hendraanggrian.openpss.schema.Employees
import com.hendraanggrian.openpss.schema.GlobalSettings
import com.hendraanggrian.openpss.schema.Invoices
import com.hendraanggrian.openpss.schema.Logs
import com.hendraanggrian.openpss.schema.OffsetPrices
import com.hendraanggrian.openpss.schema.Payments
import com.hendraanggrian.openpss.schema.PlatePrices
import com.hendraanggrian.openpss.schema.Recesses
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

object Database {
    lateinit var DATABASE: MongoDB
    private val TABLES: Array<DocumentSchema<String, out Document<*>>> = arrayOf(
        Customers,
        DigitalPrices,
        Employees,
        Logs,
        GlobalSettings,
        Invoices,
        OffsetPrices,
        Payments,
        PlatePrices,
        Recesses,
        Wages
    )

    fun connect() {
        DATABASE = MongoDB(
            arrayOf(ServerAddress(ServerAddress.defaultHost(), ServerAddress.defaultPort())),
            BuildConfig.DATABASE,
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
            listOf(GlobalSettings.LANGUAGE, GlobalSettings.INVOICE_HEADERS)
                .filter { pair -> GlobalSettings { key.equal(pair.first) }.isEmpty() }
                .forEach { GlobalSettings += GlobalSetting(it.first, it.second) }
        }
    }

    /** Date and time of server. */
    fun dateTime(): DateTime = DateTime(evalDate)

    /** Local date of server. */
    fun date(): LocalDate = LocalDate.fromDateFields(evalDate)

    /** Local time of server. */
    fun time(): LocalTime = LocalTime.fromDateFields(evalDate)

    @Suppress("DEPRECATION")
    private inline val evalDate: Date
        get() = DATABASE.db.doEval("new Date()").getDate("retval")
}

/**
 * A failed transaction will most likely throw an exception instance of [MongoException].
 * This function will safely execute a transaction and display an error log on JavaFX if it throws those exceptions.
 *
 * @see [MongoDB.withSession]
 */
@Throws(Exception::class)
fun <T> transaction(statement: SessionWrapper.() -> T): T = runCatching {
    Database.DATABASE.withSession { SessionWrapper(this).statement() }
}.getOrElse {
    if (BuildConfig.DEBUG) it.printStackTrace()
    error("Connection closed. Please sign in again.")
}