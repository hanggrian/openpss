package com.hendraanggrian.openpss.api

import com.fatboyindustrial.gsonjodatime.Converters
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.takeFrom

abstract class Api(private val endPoint: String) {

    protected val client = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                Converters.registerLocalDate(this)
                Converters.registerLocalTime(this)
                Converters.registerLocalDateTime(this)
                Converters.registerDateTime(this)
            }
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
}