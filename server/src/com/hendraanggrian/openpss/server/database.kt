package com.hendraanggrian.openpss.server

import com.hendraanggrian.openpss.data.Document
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.GlobalSetting
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
import javafx.util.Builder
import kotlinx.nosql.AbstractColumn
import kotlinx.nosql.AbstractSchema
import kotlinx.nosql.AndQuery
import kotlinx.nosql.DocumentSchemaOperations
import kotlinx.nosql.Id
import kotlinx.nosql.IndexOperations
import kotlinx.nosql.Query
import kotlinx.nosql.Session
import kotlinx.nosql.TableSchemaOperations
import kotlinx.nosql.equal
import kotlinx.nosql.id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.mongodb.MongoDB
import kotlinx.nosql.mongodb.MongoDBSession
import kotlinx.nosql.query.NoQuery
import kotlinx.nosql.query.OrQuery
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.util.Date

private lateinit var DATABASE: MongoDB
private val TABLES: Array<DocumentSchema<out Document<out DocumentSchema<out Document<out DocumentSchema<*>>>>>> =
    arrayOf(
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
@Throws(Exception::class)
fun <T> transaction(statement: SessionWrapper.() -> T): T = runCatching {
    DATABASE.withSession { SessionWrapper(this).statement() }
}.getOrElse {
    error("Connection closed. Please sign in again.")
}

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
fun dbDateTime(): DateTime = DateTime(evalDate)

/** Local date of server. */
fun dbDate(): LocalDate = LocalDate.fromDateFields(evalDate)

/** Local time of server. */
fun dbTime(): LocalTime = LocalTime.fromDateFields(evalDate)

@Suppress("DEPRECATION")
private inline val evalDate: Date
    get() = DATABASE.db.doEval("new Date()").getDate("retval")

typealias DocumentQuery<T, P, C> = kotlinx.nosql.DocumentSchemaQueryWrapper<T, P, C>

class QueryBuilder : Builder<Query> {
    private var source: Query? = null

    fun and(target: Query): Unit = setOrCreate(target) { AndQuery(it, target) }

    fun or(target: Query): Unit = setOrCreate(target) { OrQuery(it, target) }

    override fun build(): Query = source ?: NoQuery

    private fun setOrCreate(target: Query, creator: (Query) -> Query) {
        source = source?.let { creator(it) } ?: target
    }
}

/** Extended version of [MongoDBSession]. */
class SessionWrapper(val session: MongoDBSession) : Session by session,
    DocumentSchemaOperations by session,
    TableSchemaOperations by session,
    IndexOperations by session {

    /** Use shorter `plusAssign` operator when inserted document's id is unused. */
    operator fun <S : DocumentSchema<D>, D : Document<S>> S.plusAssign(
        document: D
    ) {
        insert(document)
    }

    /** Same with `MongoDBSession`'s `find` but moved schema instance from receiver to parameter. */
    operator fun <S : DocumentSchema<D>, D : Document<S>> S.invoke(
        query: S.() -> Query = { NoQuery }
    ): DocumentQuery<S, String, D> = session.run { return find(query) }

    /** Realm-style find by id. */
    operator fun <S : DocumentSchema<D>, D : Document<S>> S.get(
        id: Id<String, S>
    ): DocumentQuery<S, String, D> = invoke { this.id.equal(id) }

    /** Find by id associated with document. */
    operator fun <S : DocumentSchema<D>, D : Document<S>> S.get(
        document: D
    ): DocumentQuery<S, String, D> = get(document.id)

    operator fun <S : DocumentSchema<D>, D : Document<S>> S.get(
        idValue: String
    ): DocumentQuery<S, String, D> = get(Id(idValue))

    operator fun <S : DocumentSchema<D>, D : Document<S>> S.minusAssign(
        id: Id<String, S>
    ) {
        get(id).remove()
    }

    operator fun <S : DocumentSchema<D>, D : Document<S>> S.minusAssign(
        document: D
    ) {
        get(document).remove()
    }

    /** Build query for optional and/or query operation. */
    fun <S : DocumentSchema<D>, D : Document<S>> S.buildQuery(
        builder: S.(
            and: (target: Query) -> Unit,
            or: (target: Query) -> Unit
        ) -> Unit
    ): DocumentQuery<S, String, D> = invoke {
        QueryBuilder().apply { builder({ and(it) }) { or(it) } }.build()
    }

    fun findGlobalSetting(key: String): GlobalSetting = GlobalSettings { this.key.equal(key) }.single()

    /** Matches with [regex] automatically transformed to pattern with certain [flags]. */
    fun <T : AbstractSchema, C> AbstractColumn<out C?, T, *>.matches(
        regex: Any,
        flags: Int = 0
    ): Query = matches("$regex".toPattern(flags))
}