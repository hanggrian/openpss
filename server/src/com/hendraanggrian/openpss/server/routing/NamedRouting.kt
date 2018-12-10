package com.hendraanggrian.openpss.server.routing

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
import com.hendraanggrian.openpss.server.db.transaction
import com.hendraanggrian.openpss.util.isNotEmpty
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.update

object PlatePriceRouting : NamedRouting<PlatePrices, PlatePrice>("plate-prices", PlatePrices) {

    override fun onCreate(call: ApplicationCall): PlatePrice = PlatePrice.new(call.getString("name"))

    override fun SessionWrapper.onEdit(call: ApplicationCall, query: DocumentQuery<PlatePrices, String, PlatePrice>) {
        query.projection { price }
            .update(call.getDouble("price"))
    }
}

object OffsetPriceRouting : NamedRouting<OffsetPrices, OffsetPrice>("offset-prices", OffsetPrices) {

    override fun onCreate(call: ApplicationCall): OffsetPrice = OffsetPrice.new(call.getString("name"))

    override fun SessionWrapper.onEdit(call: ApplicationCall, query: DocumentQuery<OffsetPrices, String, OffsetPrice>) {
        query.projection { minQty + minPrice + excessPrice }
            .update(call.getInt("minQty"), call.getDouble("minPrice"), call.getDouble("excessPrice"))
    }
}

object DigitalPriceRouting : NamedRouting<DigitalPrices, DigitalPrice>("digital-prices", DigitalPrices) {

    override fun onCreate(call: ApplicationCall): DigitalPrice = DigitalPrice.new(call.getString("name"))

    override fun SessionWrapper.onEdit(
        call: ApplicationCall,
        query: DocumentQuery<DigitalPrices, String, DigitalPrice>
    ) {
        query.projection { oneSidePrice + twoSidePrice }
            .update(call.getDouble("oneSidePrice"), call.getDouble("twoSidePrice"))
    }
}

object EmployeeRouting : NamedRouting<Employees, Employee>("employees", Employees) {

    override fun onCreate(call: ApplicationCall): Employee = Employee.new(call.getString("name"))

    override fun SessionWrapper.onEdit(call: ApplicationCall, query: DocumentQuery<Employees, String, Employee>) {
        query.projection { password + isAdmin }
            .update(call.getString("password"), call.getBoolean("isAdmin"))
        Logs += Log.new(
            resources.getString(R.string.employee_edit).format(query.single().name),
            call.getString("login")
        )
    }

    override fun SessionWrapper.onDeleted(call: ApplicationCall, query: DocumentQuery<Employees, String, Employee>) {
        Logs += Log.new(
            resources.getString(R.string.employee_delete).format(query.single().name),
            call.getString("login")
        )
    }
}

sealed class NamedRouting<S, D>(path: String, val schema: S) : Routing(path)
    where S : DocumentSchema<D>, S : NamedSchema, D : Document<S>, D : Named {

    abstract fun onCreate(call: ApplicationCall): D

    abstract fun SessionWrapper.onEdit(call: ApplicationCall, query: DocumentQuery<S, String, D>)

    open fun SessionWrapper.onDeleted(call: ApplicationCall, query: DocumentQuery<S, String, D>) {}

    override fun Route.onInvoke() {
        get {
            call.respond(transaction { schema() })
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
        route("{name}") {
            get {
                call.respond(transaction { schema { it.name.equal(call.getString("name")) }.single() })
            }
            put {
                transaction {
                    onEdit(call, schema { it.name.equal(call.getString("name")) })
                }
                call.respond(HttpStatusCode.OK)
            }
            delete {
                transaction {
                    val query = schema { it.name.equal(call.getString("name")) }
                    schema -= query.single()
                    onDeleted(call, query)
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}