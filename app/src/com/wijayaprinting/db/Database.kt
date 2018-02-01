package com.wijayaprinting.db

import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential.createCredential
import com.mongodb.ServerAddress
import com.wijayaprinting.BuildConfig.ARTIFACT
import com.wijayaprinting.collections.isEmpty
import com.wijayaprinting.db.dao.Config
import com.wijayaprinting.db.dao.Config.Companion.DEFAULT_TIMEZONE
import com.wijayaprinting.db.dao.Config.Companion.KEY_TIMEZONE
import com.wijayaprinting.db.dao.Employee
import com.wijayaprinting.db.schema.*
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
            var timezoneConfig = Configs.find { key.equal(KEY_TIMEZONE) }.firstOrNull()
            if (timezoneConfig == null) {
                timezoneConfig = Config(KEY_TIMEZONE, DEFAULT_TIMEZONE)
                timezoneConfig.id = Configs.insert(timezoneConfig)
            }
            check(timezoneConfig.value == getInstance().timeZone.id) { "TimeZone mismatch! Expecting ${timezoneConfig.value}" }

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
}