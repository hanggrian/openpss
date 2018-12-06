package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.NamedSchema
import com.hendraanggrian.openpss.db.schemas.OffsetPrice
import com.hendraanggrian.openpss.db.schemas.OffsetPrices
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import com.hendraanggrian.openpss.db.schemas.PlatePrices
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.util.pipeline.PipelineContext
import kotlinx.nosql.equal
import kotlinx.nosql.mongodb.DocumentSchema

fun Routing.routePlatePrice() = routeNamed("plate-price", PlatePrices) { PlatePrice.new(call.parameters["name"]!!) }

fun Routing.routeOffsetPrice() = routeNamed("offset-price", OffsetPrices) { OffsetPrice.new(call.parameters["name"]!!) }

private fun <S, D : Document<S>> Routing.routeNamed(
    route: String,
    schema: S,
    create: PipelineContext<*, ApplicationCall>.() -> D
) where S : DocumentSchema<D>, S : NamedSchema {
    route(route) {
        get {
            call.respond(transaction { schema() })
        }
        post {
            val doc = create()
            doc.id = transaction { schema.insert(doc) }
            call.respond(doc)
        }
        route("{name}") {
            put {

            }
            delete {
                call.respond(transaction {
                    val doc = schema { it.name.equal(call.parameters["name"]) }.single()
                    schema -= doc
                    doc
                })
            }
        }
    }
}