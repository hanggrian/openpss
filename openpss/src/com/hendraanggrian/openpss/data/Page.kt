package com.hendraanggrian.openpss.data

data class Page<T>(
    val pageCount: Int,
    val items: List<T>
)
