@file:Suppress("ktlint:rulebook:exception-subclass-catching")

package com.hanggrian.openpss.ui.main.help

import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.annotations.SerializedName
import com.hanggrian.openpss.BuildConfig
import com.hanggrian.openpss.Context
import com.hanggrian.openpss.OpenPssApp
import com.hanggrian.openpss.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.jfoenix.controls.jfxSnackbar
import okhttp3.OkHttpClient
import org.apache.maven.artifact.versioning.ComparableVersion
import retrofit2.Retrofit
import retrofit2.adapter.guava.GuavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.URI
import java.util.concurrent.TimeUnit.SECONDS

/** As seen in `https://developer.github.com/v3/`. */
interface GitHubApi {
    @GET("repos/${BuildConfig.AUTHOR}/${BuildConfig.ARTIFACT}/releases/latest")
    fun getLatestRelease(): ListenableFuture<Release>

    companion object {
        private const val END_POINT = "https://api.github.com"
        private const val TIMEOUT = 5L

        private fun create(): GitHubApi =
            Retrofit
                .Builder()
                .client(
                    OkHttpClient
                        .Builder()
                        .addInterceptor {
                            it.proceed(
                                it
                                    .request()
                                    .newBuilder()
                                    .addHeader("Accept", "application/json")
                                    .build(),
                            )
                        }.build(),
                ).baseUrl(END_POINT)
                .addCallAdapterFactory(GuavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GitHubApi::class.java)

        fun checkUpdates(context: Context) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val release = create().getLatestRelease().get(TIMEOUT, SECONDS)
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        when {
                            release.isNewer() ->
                                context.stack.jfxSnackbar(
                                    context.getString(
                                        R.string_openpss_is_available,
                                        release.version,
                                    ),
                                    OpenPssApp.DURATION_LONG,
                                    context.getString(R.string_download),
                                ) {
                                    UpdateDialog(context, release.assets).show { url ->
                                        url?.let { context.desktop?.browse(URI(it)) }
                                    }
                                }
                            else ->
                                context.stack.jfxSnackbar(
                                    context.getString(
                                        R.string_openpss_is_currently_the_newest_version_available,
                                        BuildConfig.VERSION,
                                    ),
                                    OpenPssApp.DURATION_SHORT,
                                )
                        }
                    }
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) e.printStackTrace()
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        context.stack.jfxSnackbar(
                            context.getString(R.string_no_internet_connection),
                            OpenPssApp.DURATION_SHORT,
                        )
                    }
                }
            }
        }
    }

    data class Release(
        @SerializedName("name") val version: String,
        @SerializedName("assets") val assets: List<Asset>,
    ) {
        fun isNewer(): Boolean =
            ComparableVersion(version) > ComparableVersion(BuildConfig.VERSION) &&
                assets.isNotEmpty() &&
                assets.all { it.isUploaded() }
    }

    data class Asset(
        @SerializedName("browser_download_url") val downloadUrl: String,
        @SerializedName("name") val name: String,
        @SerializedName("state") val state: String,
    ) {
        fun isUploaded(): Boolean = state == "uploaded"

        override fun toString(): String = name
    }
}
