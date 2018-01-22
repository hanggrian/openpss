package com.wijayaprinting.db

import com.mongodb.MongoCredential.createCredential
import com.mongodb.ServerAddress
import com.wijayaprinting.BuildConfig.ARTIFACT
import com.wijayaprinting.collections.isEmpty
import com.wijayaprinting.db.dao.Employee
import com.wijayaprinting.db.dao.Wages
import com.wijayaprinting.db.schema.*
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB
import java.net.InetAddress.getByName

/** Connect to MongoDB database using RxJava's streams. */
object Database {
    lateinit var INSTANCE: MongoDB

    fun login(host: String, port: Int, user: String, password: String, employeeName: String, employeePassword: String): Single<Employee> = Single.create { emitter ->
        try {
            var employee: Employee? = null
            INSTANCE = connect(host, port, user, password)
            transaction {
                if (Employees.find { name.equal(Employee.name) }.isEmpty) Employees.insert(Employee)
                employee = checkNotNull(Employees.find { name.equal(employeeName) }.singleOrNull()) { "Employee not found!" }
                require(employee!!.password == employeePassword) { "Invalid password!" }
            }
            employee!!.clearPassword()
            emitter.onSuccess(employee!!)
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    fun testConnection(host: String, port: Int, user: String, password: String): Completable = Completable.create { emitter ->
        try {
            connect(host, port, user, password)
            emitter.onComplete()
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    @Throws(Exception::class)
    private fun connect(host: String, port: Int, user: String, password: String): MongoDB {
        check(getByName(host).isReachable(3000)) { "IP address unreachable!" }
        return MongoDB(arrayOf(ServerAddress(host, port)), ARTIFACT, arrayOf(createCredential(user, "admin", password.toCharArray())),
                schemas = arrayOf(Customers, Employees, Offsets, PlateOrders, OffsetOrders, Plates, Receipts, Recesses, Wages))
    }
}