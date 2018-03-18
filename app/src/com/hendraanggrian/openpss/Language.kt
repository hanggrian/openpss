package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.util.capitalizeAll
import java.util.Locale
import java.util.ResourceBundle
import java.util.ResourceBundle.getBundle

enum class Language(
    val code: String,
    private val country: String
) {
    ENGLISH("en", "US"),
    BAHASA_INDONESIA("in", "ID");

    val resources: ResourceBundle get() = getBundle("string", toLocale())

    fun toLocale(): Locale = Locale(code, country)

    override fun toString(): String = name.replace("_", " ").toLowerCase().capitalizeAll()

    companion object {
        fun find(languageCode: String): Language = values().single { it.code == languageCode }
    }
}