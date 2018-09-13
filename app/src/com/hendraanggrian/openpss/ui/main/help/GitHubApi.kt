package com.hendraanggrian.openpss.ui.main.help

import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.annotations.SerializedName
import com.hendraanggrian.openpss.BuildConfig.ARTIFACT
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.BuildConfig.USER
import com.hendraanggrian.openpss.BuildConfig.VERSION
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.desktop
import javafxx.scene.control.errorAlert
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import org.apache.maven.artifact.versioning.ComparableVersion
import org.controlsfx.control.action.Action
import retrofit2.Retrofit
import retrofit2.adapter.guava.GuavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.URI
import java.util.concurrent.TimeUnit.SECONDS

/** As seen in `https://developer.github.com/v3/`. */
interface GitHubApi {

    @GET("repos/$USER/$ARTIFACT/releases/latest")
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
    }

    companion object {
        private const val END_POINT = "https://api.github.com"
        private const val TIMEOUT: Long = 5

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

        fun checkUpdates(
            resourced: Resourced,
            onAvailable: (title: String, actions: List<Action>) -> Unit,
            onUnavailable: (title: String, content: String) -> Unit
        ) {
            GlobalScope.launch(Dispatchers.Default) {
                try {
                    val release = create().getLatestRelease().get(TIMEOUT, SECONDS)
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        when {
                            release.isNewer() -> onAvailable(
                                resourced.getString(R.string.openpss_is_available, release.version),
                                release.assets.map { asset ->
                                    Action(asset.name) {
                                        desktop?.browse(URI(asset.downloadUrl))
                                    }
                                })
                            else -> onUnavailable(
                                resourced.getString(R.string.you_re_up_to_date),
                                resourced.getString(R.string.openpss_is_currently_the_newest_version_available, VERSION)
                            )
                        }
                    }
                } catch (e: Exception) {
                    if (DEBUG) e.printStackTrace()
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        errorAlert(resourced.getString(R.string.no_internet_connection)).show()
                    }
                }
            }
        }
    }
}