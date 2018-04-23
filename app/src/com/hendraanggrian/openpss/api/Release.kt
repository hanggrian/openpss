package com.hendraanggrian.openpss.api

import com.google.gson.annotations.SerializedName
import com.hendraanggrian.openpss.BuildConfig.VERSION

data class Release(
    val name: String,
    val assets: List<Asset>
) {

    fun isNewer(): Boolean = (name.versionMinor > VERSION.versionMinor || name.versionMajor > VERSION.versionMajor) &&
        assets.isNotEmpty() && assets.single().isUploaded()

    private inline val String.versionMinor: Int get() = substringBefore('.').toInt()

    private inline val String.versionMajor: Int get() = substringAfter('.').toInt()

    data class Asset(
        @SerializedName("browser_download_url") val downloadUrl: String,
        val name: String,
        val state: String
    ) {

        fun isUploaded(): Boolean = state == "uploaded"
    }
}