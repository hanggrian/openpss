package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.utils.capitalizeAll
import java.util.Locale

enum class Language(
    val code: String,
    private val country: String
) {
    ENGLISH("en", "US"),
    BAHASA_INDONESIA("in", "ID");

    fun toLocale(): Locale = Locale(code, country)

    override fun toString(): String = name.replace("_", " ").toLowerCase().capitalizeAll()

    companion object {
        fun find(code: String): Language = values().single { it.code == code }
    }
}