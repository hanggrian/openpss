package com.hendraanggrian.openpss.api.github

import com.google.gson.annotations.SerializedName

data class Asset(
    @SerializedName("browser_download_url") val downloadUrl: String,
    val name: String,
    val state: String
) {

    fun isUploaded(): Boolean = state == "uploaded"

    override fun toString(): String = name
}