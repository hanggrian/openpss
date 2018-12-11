package com.hendraanggrian.openpss.server.routing

import io.ktor.routing.Route
import io.ktor.routing.route

class RouteWrapper(private val route: Route) {

    operator fun String.invoke(block: Route.() -> Unit): Route = route.route(this, block)
}