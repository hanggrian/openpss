package com.hendraanggrian.openpss.data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    @SerializedName("page_count") val pageCount: Int,
    val items: List<T>
)