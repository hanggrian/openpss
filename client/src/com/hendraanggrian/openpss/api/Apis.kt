package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.BuildConfig2
import com.hendraanggrian.openpss.data.Release
import com.hendraanggrian.openpss.registerJodaTimeSerializers
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.response.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom

interface Api {

    val endPoint: String

    val client: HttpClient

    fun HttpRequestBuilder.jsonBody(body: Any) {
        contentType(ContentType.Application.Json)
        this.body = body
    }

    fun HttpRequestBuilder.apiUrl(path: String) {
        header(HttpHeaders.CacheControl, "no-cache")
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }

    fun HttpRequestBuilder.parameters(vararg pairs: Pair<String, Any?>) =
        pairs.forEach { (key, value) -> parameter(key, value) }

    suspend fun HttpClient.requestStatus(
        method: HttpMethod,
        block: HttpRequestBuilder.() -> Unit
    ): Boolean =
        request<HttpResponse> {
            this.method = method
            block()
        }.use { it.status.isSuccess() }
}

/** GitHub API used to check latest version. */
class GitHubApi : OkHttpApi("https://api.github.com") {

    suspend fun getLatestRelease(): Release = client.get {
        apiUrl("repos/${BuildConfig2.USER}/${BuildConfig2.ARTIFACT}/releases/latest")
    }
}

/** Main API. */
class OpenPssApi(host: String = "localhost", port: Int = 8080) : OkHttpApi("http://$host:$port"),
    AuthApi,
    CustomerApi,
    DateTimeApi,
    InvoiceApi,
    LogApi,
    NamedApi,
    PaymentApi,
    RecessApi,
    SettingApi,
    WageApi

/** Base class of REST APIs, where client is Android and Java-friendly OkHttp. */
sealed class OkHttpApi(final override val endPoint: String) : Api {

    final override val client: HttpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                registerJodaTimeSerializers()
            }
        }
    }
}