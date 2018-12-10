package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.util.jodaTimeSupport
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature

/** Base class of REST APIs, where client is Android and Java-friendly OkHttp. */
abstract class Api(final override val endPoint: String) : Route {

    final override val client: HttpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = GsonSerializer { jodaTimeSupport() }
        }
    }
}