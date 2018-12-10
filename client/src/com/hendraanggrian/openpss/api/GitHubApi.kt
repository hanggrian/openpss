package com.hendraanggrian.openpss.api

import com.google.gson.annotations.SerializedName
import io.ktor.client.request.get
import org.apache.maven.artifact.versioning.ComparableVersion

class GitHubApi : Api("https://api.github.com") {

    suspend fun getLatestRelease() = client.get<Release> {
        apiUrl("repos/hendraanggrian/openpss/releases/latest")
    }

    data class Release(
        val name: String,
        val assets: List<Asset>
    ) {

        fun isNewerThan(currentVersion: String): Boolean =
            ComparableVersion(name) > ComparableVersion(currentVersion) &&
                assets.isNotEmpty() &&
                assets.all { it.isUploaded() }
    }

    data class Asset(
        @SerializedName("browser_download_url") val downloadUrl: String,
        val name: String,
        val state: String
    ) {

        fun isUploaded(): Boolean = state == "uploaded"

        override fun toString(): String = name
    }
}