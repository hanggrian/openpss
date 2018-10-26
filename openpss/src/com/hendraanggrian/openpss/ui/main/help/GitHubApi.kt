package com.hendraanggrian.openpss.ui.main.help

import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.annotations.SerializedName
import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.BuildConfig.ARTIFACT
import com.hendraanggrian.openpss.BuildConfig.AUTHOR
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.BuildConfig.VERSION
import com.hendraanggrian.openpss.Context
import com.hendraanggrian.openpss.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.jfoenix.jfxSnackbar
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

    @GET("repos/$AUTHOR/$ARTIFACT/releases/latest")
    fun getLatestRelease(): ListenableFuture<Release>

    data class Release(
        @SerializedName("name") val version: String,
        @SerializedName("assets") val assets: List<Asset>
    ) {

        fun isNewer(): Boolean = ComparableVersion(version) > ComparableVersion(VERSION) &&
            assets.isNotEmpty() &&
            assets.all { it.isUploaded() }
    }

    data class Asset(
        @SerializedName("browser_download_url") val downloadUrl: String,
        @SerializedName("name") val name: String,
        @SerializedName("state") val state: String
    ) {

        fun isUploaded(): Boolean = state == "uploaded"

        override fun toString(): String = name
    }

    companion object {
        private const val END_POINT = "https://api.github.com"
        private const val TIMEOUT = 5L

        private fun create(): GitHubApi = Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor {
                it.proceed(
                    it.request()
                        .newBuilder()
                        .addHeader("Accept", "application/json")
                        .build()
                )
            }.build())
            .baseUrl(END_POINT)
            .addCallAdapterFactory(GuavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)

        fun checkUpdates(context: Context) {
            GlobalScope.launch(Dispatchers.Default) {
                try {
                    val release = create().getLatestRelease().get(TIMEOUT, SECONDS)
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        when {
                            release.isNewer() -> context.root.jfxSnackbar(
                                context.getString(R.string.openpss_is_available, release.version),
                                App.DURATION_LONG,
                                context.getString(R.string.download)
                            ) {
                                UpdateDialog(context, release.assets).show { url ->
                                    context.desktop?.browse(URI(url))
                                }
                            }
                            else -> context.root.jfxSnackbar(
                                context.getString(
                                    R.string.openpss_is_currently_the_newest_version_available,
                                    VERSION
                                ),
                                App.DURATION_SHORT
                            )
                        }
                    }
                } catch (e: Exception) {
                    if (DEBUG) e.printStackTrace()
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        context.root.jfxSnackbar(context.getString(R.string.no_internet_connection), App.DURATION_SHORT)
                    }
                }
            }
        }
    }
}