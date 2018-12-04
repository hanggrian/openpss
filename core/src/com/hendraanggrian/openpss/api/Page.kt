package com.hendraanggrian.openpss.api

data class Page<T>(
    val pageCount: Int,
    val items: List<T>
)