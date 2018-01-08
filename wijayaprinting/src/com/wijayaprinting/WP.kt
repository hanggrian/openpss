package com.wijayaprinting

import com.mysql.jdbc.Driver
import com.wijayaprinting.dao.*
import io.reactivex.Completable
import io.reactivex.Single
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.InetAddress.getByName

object WP {
    private const val DATABASE = "wp"
    private val TABLES = arrayOf(Customers, Employees, Payments, Plates, PlateOrders, PlateReceipts, Recesses, Receipts, Wages)

    /** Connect to mysql database, emitting boolean value determining whether or not user logged in has full access. */
    fun login(ip: String, host: String, user: String, password: String, employeeId: String, employeePassword: String): Single<Employee> = Single.create<Employee> { emitter ->
        try {
            connect(ip, host, user, password)
            var employee: Employee? = null
            transaction {
                createMissing()
                employee = checkNotNull(Employee.findById(employeeId)) { "Employee not found!" }
                require(employee!!.password == employeePassword) { "Invalid password!" }
            }
            emitter.onSuccess(employee!!)
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    fun testConnection(ip: String, host: String, user: String, password: String): Completable = Completable.create { emitter ->
        try {
            connect(ip, host, user, password)
            transaction { createMissing() }
            emitter.onComplete()
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    @Throws(Exception::class)
    private fun connect(ip: String, host: String, user: String, password: String) {
        check(getByName(ip).isReachable(3000)) { "IP address unreachable!" }
        Database.connect("jdbc:mysql://$ip:$host/$DATABASE", Driver::class.java.canonicalName, user, password)
    }

    private fun createMissing() = createMissingTablesAndColumns(*TABLES)
}