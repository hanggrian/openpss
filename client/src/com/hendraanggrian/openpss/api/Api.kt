package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.util.jodaTimeSupport
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.response.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom

abstract class Api(private val endPoint: String) {

    protected val client: HttpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = GsonSerializer { jodaTimeSupport() }
        }
    }

    protected fun HttpRequestBuilder.json() = contentType(ContentType.Application.Json)

    protected fun HttpRequestBuilder.apiUrl(path: String) {
        header(HttpHeaders.CacheControl, "no-cache")
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }

    protected fun HttpRequestBuilder.parameters(vararg pairs: Pair<String, Any?>) = pairs.forEach { (key, value) ->
        parameter(key, value)
    }

    protected fun HttpResponse.useStatus(): Boolean = use { it.status.isSuccess() }
}