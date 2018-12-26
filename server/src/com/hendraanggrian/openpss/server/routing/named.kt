package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.data.DigitalPrice
import com.hendraanggrian.openpss.data.Document
import com.hendraanggrian.openpss.data.Employee
import com.hendraanggrian.openpss.data.Log
import com.hendraanggrian.openpss.data.Named
import com.hendraanggrian.openpss.data.OffsetPrice
import com.hendraanggrian.openpss.data.PlatePrice
import com.hendraanggrian.openpss.schema.DigitalPrices
import com.hendraanggrian.openpss.schema.Employees
import com.hendraanggrian.openpss.schema.Logs
import com.hendraanggrian.openpss.schema.NamedSchema
import com.hendraanggrian.openpss.schema.OffsetPrices
import com.hendraanggrian.openpss.schema.PlatePrices
import com.hendraanggrian.openpss.server.db.DocumentQuery
import com.hendraanggrian.openpss.server.db.wrapper
import com.hendraanggrian.openpss.server.transaction
import com.hendraanggrian.openpss.util.isNotEmpty
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.update

fun Routing.platePriceRouting() = namedRouting("plate-prices", PlatePrices,
    onCreate = { call -> PlatePrice.new(call.getString("name")) },
    onEdit = { call, query ->
        query.projection { price }
            .update(call.getDouble("price"))
    })

fun Routing.offsetPriceRouting() = namedRouting("offset-prices", OffsetPrices,
    onCreate = { call -> OffsetPrice.new(call.getString("name")) },
    onEdit = { call, query ->
        query.projection { minQty + minPrice + excessPrice }
            .update(call.getInt("minQty"), call.getDouble("minPrice"), call.getDouble("excessPrice"))
    })

fun Routing.digitalPriceRouting() = namedRouting("digital-prices", DigitalPrices,
    onCreate = { call -> DigitalPrice.new(call.getString("name")) },
    onEdit = { call, query ->
        query.projection { oneSidePrice + twoSidePrice }
            .update(call.getDouble("oneSidePrice"), call.getDouble("twoSidePrice"))
    })

fun Routing.employeeRouting() = namedRouting("employees", Employees,
    onGet = { call ->
        val employees = Employees()
        employees.forEach { it.clearPassword() }
        employees.toList()
    },
    onCreate = { call -> Employee.new(call.getString("name")) },
    onEdit = { call, query ->
        query.projection { password + isAdmin }
            .update(call.getString("password"), call.getBoolean("isAdmin"))
        Logs += Log.new(
            resources.getString(R.string.employee_edit).format(query.single().name),
            call.getString("login")
        )
    },
    onDeleted = { call, query ->
        Logs += Log.new(
            resources.getString(R.string.employee_delete).format(query.single().name),
            call.getString("login")
        )
    })

private fun <S, D> Routing.namedRouting(
    path: String,
    schema: S,
    onGet: wrapper.(call: ApplicationCall) -> List<D> = { schema().toList() },
    onCreate: (call: ApplicationCall) -> D,
    onEdit: wrapper.(call: ApplicationCall, query: DocumentQuery<S, String, D>) -> Unit,
    onDeleted: wrapper.(call: ApplicationCall, query: DocumentQuery<S, String, D>) -> Unit = { _, _ -> }
) where S : DocumentSchema<D>, S : NamedSchema, D : Document<S>, D : Named {
    route(path) {
        get {
            call.respond(transaction { onGet(call) })
        }
        post {
            val doc = onCreate(call)
            when {
                transaction { schema { it.name.equal(doc.name) }.isNotEmpty() } ->
                    call.respond(HttpStatusCode.NotAcceptable, "Name taken")
                else -> {
                    doc.id = transaction { schema.insert(doc) }
                    call.respond(doc)
                }
            }
        }
        route("{id}") {
            get {
                call.respond(transaction { schema[call.getString("id")].single() })
            }
            put {
                transaction {
                    onEdit(call, schema[call.getString("id")])
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