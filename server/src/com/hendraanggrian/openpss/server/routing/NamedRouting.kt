package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import com.hendraanggrian.openpss.db.SessionWrapper
import com.hendraanggrian.openpss.db.schemas.DigitalPrice
import com.hendraanggrian.openpss.db.schemas.DigitalPrices
import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.db.schemas.Employees
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
import kotlinx.nosql.DocumentSchemaQueryWrapper
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.update

object PlatePriceRouting : NamedRouting<PlatePrices, PlatePrice>("plate-prices", PlatePrices) {

    override fun onCreate(call: ApplicationCall): PlatePrice = PlatePrice.new(call.getString("name"))

    override fun SessionWrapper.onEdit(
        call: ApplicationCall,
        documents: DocumentSchemaQueryWrapper<PlatePrices, String, PlatePrice>
    ) {
        documents.projection { price }
            .update(call.getDouble("price"))
    }
}

object OffsetPriceRouting : NamedRouting<OffsetPrices, OffsetPrice>("offset-prices", OffsetPrices) {

    override fun onCreate(call: ApplicationCall): OffsetPrice = OffsetPrice.new(call.getString("name"))

    override fun SessionWrapper.onEdit(
        call: ApplicationCall,
        documents: DocumentSchemaQueryWrapper<OffsetPrices, String, OffsetPrice>
    ) {
        documents.projection { minQty + minPrice + excessPrice }
            .update(call.getInt("minQty"), call.getDouble("minPrice"), call.getDouble("excessPrice"))
    }
}

object DigitalPriceRouting : NamedRouting<DigitalPrices, DigitalPrice>("digital-prices", DigitalPrices) {

    override fun onCreate(call: ApplicationCall): DigitalPrice = DigitalPrice.new(call.getString("name"))

    override fun SessionWrapper.onEdit(
        call: ApplicationCall,
        documents: DocumentSchemaQueryWrapper<DigitalPrices, String, DigitalPrice>
    ) {
        documents.projection { oneSidePrice + twoSidePrice }
            .update(call.getDouble("oneSidePrice"), call.getDouble("twoSidePrice"))
    }
}

object EmployeeRouting : NamedRouting<Employees, Employee>("employees", Employees) {

    override fun onCreate(call: ApplicationCall): Employee = Employee.new(call.getString("name"))

    override fun SessionWrapper.onEdit(
        call: ApplicationCall,
        documents: DocumentSchemaQueryWrapper<Employees, String, Employee>
    ) {
        documents.projection { password + isAdmin }
            .update(call.getString("password"), call.getBoolean("isAdmin"))
    }
}

sealed class NamedRouting<S, D>(path: String, val schema: S) : Routing(path)
    where S : DocumentSchema<D>, S : NamedSchema, D : Document<S>, D : Named {

    abstract fun onCreate(call: ApplicationCall): D

    abstract fun SessionWrapper.onEdit(call: ApplicationCall, documents: DocumentSchemaQueryWrapper<S, String, D>)

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
                    schema -= schema { it.name.equal(call.getString("name")) }.single()
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}