package com.hendraanggrian.openpss

import java.util.Locale

enum class Language(val code: String, private val country: String) {
    ENGLISH("en", "US"),
    INDONESIA("id", "ID");

    fun toLocale(): Locale = Locale(code, country)

    companion object {
        fun from(languageCode: String): Language = values().firstOrNull { it.code == languageCode } ?: ENGLISH
    }
}