package com.wijayaprinting.nosql

import com.mongodb.MongoCredential.createCredential
import com.mongodb.ServerAddress
import com.wijayaprinting.utils.transaction
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.MongoDB
import kotlinx.nosql.mongodb.MongoDBSession
import java.net.InetAddress.getByName

/** Connect to MongoDB database using RxJava's streams. */
object NoSQL {
    private const val DATABASE = "wijayaprinting"
    private val SCHEMAS get() = arrayOf(Employees, Wages)

    private lateinit var mDB: MongoDB

    fun login(host: String, port: Int, user: String, password: String, employeeName: String, employeePassword: String): Single<Employee> = Single.create { emitter ->
        try {
            var employee: Employee? = null
            mDB = connect(host, port, user, password)
            transaction {
                employee = checkNotNull(Employees.find { this.name.equal(employeeName) }.singleOrNull()) { "Employee not found!" }
                require(employee!!.password == employeePassword) { "Invalid password!" }
            }
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

    fun <R> withSession(statement: MongoDBSession.() -> R): R = mDB.withSession(statement)

    @Throws(Exception::class)
    private fun connect(host: String, port: Int, user: String, password: String): MongoDB {
        check(getByName(host).isReachable(3000)) { "IP address unreachable!" }
        return MongoDB(arrayOf(ServerAddress(host, port)), DATABASE, arrayOf(createCredential(user, "admin", password.toCharArray())), schemas = SCHEMAS)
    }
}