package com.wijayaprinting.db

import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential.createCredential
import com.mongodb.ServerAddress
import com.wijayaprinting.BuildConfig.ARTIFACT
import com.wijayaprinting.collections.isEmpty
import com.wijayaprinting.db.dao.Config
import com.wijayaprinting.db.dao.Config.Companion.TIMEZONE_CONTINENT
import com.wijayaprinting.db.dao.Config.Companion.TIMEZONE_CONTINENT_DEFAULT
import com.wijayaprinting.db.dao.Config.Companion.TIMEZONE_COUNTRIES
import com.wijayaprinting.db.dao.Config.Companion.TIMEZONE_COUNTRIES_DEFAULT
import com.wijayaprinting.db.dao.Employee
import com.wijayaprinting.db.schema.Configs
import com.wijayaprinting.db.schema.Customers
import com.wijayaprinting.db.schema.Employees
import com.wijayaprinting.db.schema.OffsetOrders
import com.wijayaprinting.db.schema.Offsets
import com.wijayaprinting.db.schema.PlateOrders
import com.wijayaprinting.db.schema.Plates
import com.wijayaprinting.db.schema.Receipts
import com.wijayaprinting.db.schema.Recesses
import com.wijayaprinting.db.schema.Wages
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
            if (Employees.find { name.equal(Employee.name) }.isEmpty) Employees.insert(Employee)

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