package com.hendraanggrian.openpss.data

import com.google.gson.annotations.SerializedName

data class Page<T>(
    @SerializedName("page_count") val pageCount: Int,
    val items: List<T>
)