package com.hendraanggrian.openpss.db

import com.hendraanggrian.openpss.BuildConfig.ARTIFACT
import com.hendraanggrian.openpss.collections.isEmpty
import com.hendraanggrian.openpss.db.schema.Config
import com.hendraanggrian.openpss.db.schema.Config.Companion.TIMEZONE_CONTINENT
import com.hendraanggrian.openpss.db.schema.Config.Companion.TIMEZONE_CONTINENT_DEFAULT
import com.hendraanggrian.openpss.db.schema.Config.Companion.TIMEZONE_COUNTRIES
import com.hendraanggrian.openpss.db.schema.Config.Companion.TIMEZONE_COUNTRIES_DEFAULT
import com.hendraanggrian.openpss.db.schema.Employee
import com.hendraanggrian.openpss.db.schema.Configs
import com.hendraanggrian.openpss.db.schema.Customers
import com.hendraanggrian.openpss.db.schema.Employees
import com.hendraanggrian.openpss.db.schema.OffsetOrders
import com.hendraanggrian.openpss.db.schema.Offsets
import com.hendraanggrian.openpss.db.schema.PlateOrders
import com.hendraanggrian.openpss.db.schema.Plates
import com.hendraanggrian.openpss.db.schema.Receipts
import com.hendraanggrian.openpss.db.schema.Recesses
import com.hendraanggrian.openpss.db.schema.Wages
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential.createCredential
import com.mongodb.ServerAddress
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB
import java.util.Calendar.getInstance

/** Connect to MongoDB database using RxJava's streams. */
object Database {
    lateinit var INSTANCE: MongoDB

    @Throws(Exception::class)
    suspend fun login(host: String, port: Int, user: String, password: String, employeeName: String, employeePassword: String): Employee {
        var employee: Employee? = null
        INSTANCE = connect(host, port, user, password).await()
        transaction {
            // add default employee
            if (Employees.find { name.equal(Employee.name) }.isEmpty()) Employees.insert(Employee)

            // check timezone
            var timezoneContinent = Configs.find { key.equal(TIMEZONE_CONTINENT) }.firstOrNull()
            if (timezoneContinent == null) {
                timezoneContinent = Config(TIMEZONE_CONTINENT, TIMEZONE_CONTINENT_DEFAULT)
                timezoneContinent.id = Configs.insert(timezoneContinent)
            }
            var timezoneCountries = Configs.find { key.equal(TIMEZONE_COUNTRIES) }.firstOrNull()
            if (timezoneCountries == null) {
                timezoneCountries = Config(TIMEZONE_COUNTRIES, TIMEZONE_COUNTRIES_DEFAULT)
                timezoneCountries.id = Configs.insert(timezoneCountries)
            }
            timezoneCheck(timezoneContinent.value, timezoneCountries.valueList)

            // check login credentials
            employee = checkNotNull(Employees.find { name.equal(employeeName) }.singleOrNull()) { "Employee not found!" }
            check(employee!!.password == employeePassword) { "Invalid password!" }
        }
        employee!!.clearPassword()
        return employee!!
    }

    @Throws(Exception::class)
    private fun connect(host: String, port: Int, user: String, password: String): Deferred<MongoDB> = async {
        MongoDB(arrayOf(ServerAddress(host, port)),
            ARTIFACT,
            arrayOf(createCredential(user, "admin", password.toCharArray())),
            MongoClientOptions.Builder()
                .serverSelectionTimeout(3000)
                .build(),
            arrayOf(Configs, Customers, Employees, Offsets, PlateOrders, OffsetOrders, Plates, Receipts, Recesses, Wages))
    }

    @Throws(Exception::class)
    private fun timezoneCheck(
        expectedContinent: String,
        expectedCountries: List<String>
    ) = getInstance().timeZone.id.split("/").forEachIndexed { index, s ->
        when (index) {
            0 -> require(s == expectedContinent)
            1 -> require(expectedCountries.contains(s))
        }
    }
}