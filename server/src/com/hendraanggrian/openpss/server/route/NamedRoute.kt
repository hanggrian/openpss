package com.hendraanggrian.openpss.server.route

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.DocumentQuery
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.DigitalPrice
import com.hendraanggrian.openpss.db.schemas.DigitalPrices
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.server.transaction
import com.hendraanggrian.openpss.util.isNotEmpty
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.update

object PlatePriceRoute : NamedRoute<PlatePrices, PlatePrice>("plate-prices", PlatePrices,
    onCreate = { call -> PlatePrice.new(call.getString("name")) },
    onEdit = { call, query ->
        query.projection { price }
            .update(call.getDouble("price"))
    })

object OffsetPriceRoute : NamedRoute<OffsetPrices, OffsetPrice>("offset-prices", OffsetPrices,
    onCreate = { call -> OffsetPrice.new(call.getString("name")) },
    onEdit = { call, query ->
        query.projection { minQty + minPrice + excessPrice }
            .update(call.getInt("minQty"), call.getDouble("minPrice"), call.getDouble("excessPrice"))
    })

object DigitalPriceRoute : NamedRoute<DigitalPrices, DigitalPrice>("digital-prices", DigitalPrices,
    onCreate = { call -> DigitalPrice.new(call.getString("name")) },
    onEdit = { call, query ->
        query.projection { oneSidePrice + twoSidePrice }
            .update(call.getDouble("oneSidePrice"), call.getDouble("twoSidePrice"))
    })

object EmployeeRoute : NamedRoute<Employees, Employee>("employees", Employees,
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
            EmployeeRoute.resources.getString(R.string.employee_edit).format(query.single().name),
            call.getString("login")
        )
    },
    onDeleted = { call, query ->
        Logs += Log.new(
            EmployeeRoute.resources.getString(R.string.employee_delete).format(query.single().name),
            call.getString("login")
        )
    })

sealed class NamedRoute<S, D>(
    val path: String,
    val schema: S,
    val onGet: SessionWrapper.(call: ApplicationCall) -> List<D> = { schema().toList() },
    val onCreate: (call: ApplicationCall) -> D,
    val onEdit: SessionWrapper.(call: ApplicationCall, query: DocumentQuery<S, String, D>) -> Unit,
    val onDeleted: SessionWrapper.(call: ApplicationCall, query: DocumentQuery<S, String, D>) -> Unit = { _, _ -> }
) : Route({
    path {
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
        "{id}" {
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
}) where S : DocumentSchema<D>, S : NamedSchema, D : Document<S>, D : Named