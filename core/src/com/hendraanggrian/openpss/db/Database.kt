package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.DigitalPrices
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.GlobalSettings
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.db.schemas.Recesses
import com.hendraanggrian.openpss.db.schemas.Wages
import com.mongodb.MongoClientOptions
import com.mongodb.MongoClientOptions.Builder
import com.mongodb.MongoCredential
import com.mongodb.MongoCredential.createCredential
import com.mongodb.MongoException
import com.mongodb.ServerAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.util.Date

object Database {

    private lateinit var DATABASE: MongoDB
    val TABLES = arrayOf(
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

    /**
     * A failed transaction will most likely throw an exception instance of [MongoException].
     * This function will safely execute a transaction and display an error log on JavaFX if it throws those exceptions.
     *
     * @see [MongoDB.withSession]
     */
    @Throws(MongoException::class)
    fun <T> withSession(statement: SessionWrapper.() -> T): T = try {
        DATABASE.withSession { SessionWrapper(this).statement() }
    } catch (e: MongoException) {
        error("Connection closed. Please sign in again.")
    }

    fun setup() {
        DATABASE = MongoDB(
            arrayOf(ServerAddress(ServerAddress.defaultHost(), ServerAddress.defaultPort())),
            "openpss",
            arrayOf(MongoCredential.createCredential("hendraanggrian", "admin", "justforN0sql!".toCharArray())),
            MongoClientOptions.Builder().serverSelectionTimeout(3000).build(),
            TABLES
        )
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
        DATABASE = withContext(Dispatchers.Default) {
            MongoDB(
                arrayOf(ServerAddress(host, port)),
                "openpss",
                arrayOf(createCredential(user, "admin", password.toCharArray())),
                Builder().serverSelectionTimeout(3000).build(),
                TABLES
            )
        }
        lateinit var employee: Employee
        withSession {
            // check first time installation
            TABLES.mapNotNull { it as? Setupable }.forEach { it.setup(this) }
            // check login credentials
            employee = checkNotNull(Employees { it.name.equal(employeeName) }.singleOrNull()) { "Employee not found" }
            check(employee.password == employeePassword) { "Invalid password" }
        }
        employee.clearPassword()
        return employee
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

@Throws(Exception::class)
fun <T> transaction(statement: SessionWrapper.() -> T): T = Database.withSession(statement)