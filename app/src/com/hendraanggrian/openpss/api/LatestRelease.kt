package com.hendraanggrian.openpss.api

data class LatestRelease(val assets: List<Asset>) {

    data class Asset(
        val browser_download_url: String,
        val label: String,
        val state: String
    ) {

        fun isUploaded(): Boolean = state == "uploaded"
    }
}