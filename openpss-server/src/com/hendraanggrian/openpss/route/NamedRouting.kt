package com.hendraanggrian.openpss.route

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.Server
import com.hendraanggrian.openpss.nosql.DocumentQuery
import com.hendraanggrian.openpss.nosql.NamedDocument
import com.hendraanggrian.openpss.nosql.NamedDocumentSchema
import com.hendraanggrian.openpss.nosql.SessionWrapper
import com.hendraanggrian.openpss.nosql.transaction
import com.hendraanggrian.openpss.schema.DigitalPrice
import com.hendraanggrian.openpss.schema.DigitalPrices
import com.hendraanggrian.openpss.schema.Employee
import com.hendraanggrian.openpss.schema.Employees
import com.hendraanggrian.openpss.schema.Log
import com.hendraanggrian.openpss.schema.Logs
import com.hendraanggrian.openpss.schema.OffsetPrice
import com.hendraanggrian.openpss.schema.OffsetPrices
import com.hendraanggrian.openpss.schema.PlatePrice
import com.hendraanggrian.openpss.schema.PlatePrices
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import kotlin.reflect.KClass
import kotlinx.nosql.equal
import kotlinx.nosql.update

fun Routing.platePrice() = named(PlatePrices,
    PlatePrice::class,
    onEdit = { _, query, price ->
        query.projection { this.price }
            .update(price.price)
    })

fun Routing.offsetPrice() = named(
    OffsetPrices,
    OffsetPrice::class,
    onEdit = { _, query, price ->
        query.projection { minQty + minPrice + excessPrice }
            .update(price.minQty, price.minPrice, price.excessPrice)
    })

fun Routing.digitalPrice() = named(
    DigitalPrices,
    DigitalPrice::class,
    onEdit = { _, query, price ->
        query.projection { oneSidePrice + twoSidePrice }
            .update(price.oneSidePrice, price.twoSidePrice)
    })

fun Routing.employee() = named(Employees,
    Employee::class,
    onGet = {
        val employees = Employees()
        employees.forEach { it.clearPassword() }
        employees.toList()
    },
    onEdit = { call, query, employee ->
        query.projection { password + isAdmin }
            .update(employee.password, employee.isAdmin)
        Logs += Log.new(
            Server.getString(R.string.employee_edit).format(query.single().name),
            call.getString("login")
        )
    },
    onDeleted = { call, query ->
        Logs += Log.new(
            Server.getString(R.string.employee_delete).format(query.single().name),
            call.getString("login")
        )
    })

private fun <S : NamedDocumentSchema<D>, D : NamedDocument<S>> Routing.named(
    schema: S,
    klass: KClass<D>,
    onGet: SessionWrapper.(call: ApplicationCall) -> List<D> = { schema().toList() },
    onEdit: SessionWrapper.(call: ApplicationCall, query: DocumentQuery<S, String, D>, document: D) -> Unit,
    onDeleted: SessionWrapper.(call: ApplicationCall, query: DocumentQuery<S, String, D>) -> Unit = { _, _ -> }
) {
    route(schema.schemaName) {
        get {
            call.respond(transaction { onGet(call) })
        }
        post {
            val document = call.receive(klass)
            when {
                transaction { schema { name.equal(document.name) }.isNotEmpty() } ->
                    call.respond(HttpStatusCode.NotAcceptable, "Name taken")
                else -> {
                    document.id = transaction { schema.insert(document) }
                    call.respond(document)
                }
            }
        }
        route("{id}") {
            get {
                call.respond(transaction { schema[call.getString("id")].single() })
            }
            put {
                val document = call.receive(klass)
                transaction {
                    onEdit(call, schema[call.getString("id")], document)
                }
                call.respond(HttpStatusCode.OK)
            }
            delete {
                transaction {
                    val query = schema[call.getString("id")]
                    schema -= query.single()
                    onDeleted(call, query)
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
