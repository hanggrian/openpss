package com.hendraanggrian.openpss.data

data class Asset(
    val browserDownloadUrl: String,
    val name: String,
    val state: String
) {

    fun isUploaded(): Boolean = state == "uploaded"

    override fun toString(): String = name
}
