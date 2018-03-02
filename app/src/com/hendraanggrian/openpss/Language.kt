package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.util.capitalizeAll
import java.util.Locale
import java.util.ResourceBundle
import java.util.ResourceBundle.getBundle

enum class Language(val locale: String) {
    ENGLISH("en"),
    BAHASA_INDONESIA("in");

    val resources: ResourceBundle get() = getBundle("string", Locale(locale))

    override fun toString(): String = name.replace("_", " ").toLowerCase().capitalizeAll()

    companion object {
        fun from(locale: String): Language = values().first { it.locale == locale }
    }
}