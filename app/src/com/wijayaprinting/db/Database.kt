package com.wijayaprinting.db

import com.mongodb.MongoCredential.createCredential
import com.mongodb.MongoException
import com.mongodb.ServerAddress
import com.wijayaprinting.BuildConfig.ARTIFACT
import com.wijayaprinting.BuildConfig.DEBUG
import com.wijayaprinting.collections.isEmpty
import io.reactivex.Completable
import io.reactivex.Single
import javafx.application.Platform
import kotfx.errorAlert
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB
import kotlinx.nosql.mongodb.MongoDBSession
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
                schemas = arrayOf(Customers, Employees, PlateOrders, PrintOrders, Plates, Receipts, Recesses, Wages))
    }
}

/**
 * A failed transaction will most likely throw an exception instance of [MongoException].
 * This function will safely execute a transaction and display an error message on JavaFX if it throws those exceptions.
 *
 * @see [kotlinx.nosql.mongodb.MongoDB.withSession]
 */
fun <R> transaction(statement: MongoDBSession.() -> R): R? = try {
    Database.INSTANCE.withSession(statement)
} catch (e: MongoException) {
    if (DEBUG) e.printStackTrace()
    errorAlert(e.message.toString()) { headerText = "Connection closed. Please sign in again." }.showAndWait().ifPresent {
        Platform.exit()
        System.exit(0)
    }
    null
}