package com.hendraanggrian.openpss.api

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.response.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom

interface Route {

    val endPoint: String

    val client: HttpClient

    fun HttpRequestBuilder.json() = contentType(ContentType.Application.Json)

    fun HttpRequestBuilder.apiUrl(path: String) {
        header(HttpHeaders.CacheControl, "no-cache")
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }

    fun HttpRequestBuilder.parameters(vararg pairs: Pair<String, Any?>) =
        pairs.forEach { (key, value) -> parameter(key, value) }

    suspend fun HttpClient.requestStatus(block: HttpRequestBuilder.() -> Unit): Boolean =
        request<HttpResponse>(block).use { it.status.isSuccess() }
}