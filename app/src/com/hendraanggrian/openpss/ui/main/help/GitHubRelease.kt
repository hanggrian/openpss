package com.hendraanggrian.openpss.ui.main.help

import com.google.gson.annotations.SerializedName
import com.hendraanggrian.openpss.BuildConfig.VERSION
import org.apache.maven.artifact.versioning.ComparableVersion

/** As seen in `https://developer.github.com/v3/repos/releases/`. */
data class GitHubRelease(
    @SerializedName("name") val version: String,
    @SerializedName("assets") val assets: List<Asset>
) {

        assets.isNotEmpty() &&
        assets.all { it.isUploaded() }

    data class Asset(
        @SerializedName("browser_download_url") val downloadUrl: String,
        @SerializedName("name") val name: String,
        @SerializedName("state") val state: String
    ) {

        fun isUploaded(): Boolean = state == "uploaded"
    }
}