package com.hendraanggrian.openpss.ui.main.help

import com.google.gson.annotations.SerializedName
import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.BuildConfig.ARTIFACT
import com.hendraanggrian.openpss.BuildConfig.AUTHOR
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.BuildConfig.VERSION
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.api.ApiFactory
import com.hendraanggrian.openpss.content.FxComponent
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.jfoenix.jfxSnackbar
import org.apache.maven.artifact.versioning.ComparableVersion
import retrofit2.http.GET
import java.net.URI

/** As seen in `https://developer.github.com/v3/`. */
interface GitHubApi {

    @GET("repos/$AUTHOR/$ARTIFACT/releases/latest")
    fun getLatestRelease(): Deferred<Release>

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

    companion object : ApiFactory<GitHubApi>(GitHubApi::class.java, "https://api.github.com") {

        fun checkUpdates(component: FxComponent) {
            GlobalScope.launch(Dispatchers.Default) {
                try {
                    val release = api.getLatestRelease().await()
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        when {
                            release.isNewer() -> component.rootLayout.jfxSnackbar(
                                component.getString(R.string.openpss_is_available, release.version),
                                App.DURATION_LONG,
                                component.getString(R.string.download)
                            ) {
                                UpdateDialog(component, release.assets).show { url ->
                                    component.desktop?.browse(URI(url))
                                }
                            }
                            else -> component.rootLayout.jfxSnackbar(
                                component.getString(
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
                        component.rootLayout.jfxSnackbar(
                            component.getString(R.string.no_internet_connection),
                            App.DURATION_SHORT
                        )
                    }
                }
            }
        }
    }
}