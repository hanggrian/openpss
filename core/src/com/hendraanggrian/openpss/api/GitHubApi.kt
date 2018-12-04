package com.hendraanggrian.openpss.api

import com.google.gson.annotations.SerializedName
import io.ktor.client.request.get
import org.apache.maven.artifact.versioning.ComparableVersion

class GitHubApi : Api2() {

    suspend fun getLatestRelease() = client.get<Release>(
        "https", "api.github.com",
        path = "repos/hendraanggrian/openpss/releases/latest"
    )

    data class Release(
        @SerializedName("name") val version: String,
        @SerializedName("assets") val assets: List<Asset>
    ) {

        fun isNewerThan(currentVersion: String): Boolean =
            ComparableVersion(version) > ComparableVersion(currentVersion) &&
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
}