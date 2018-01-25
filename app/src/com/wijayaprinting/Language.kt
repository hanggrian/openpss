package com.wijayaprinting

import org.apache.commons.lang3.StringUtils.capitalize
import java.util.*
import java.util.ResourceBundle.getBundle

enum class Language(val locale: String) {
    ENGLISH("en"),
    BAHASA_INDONESIA("in");

    val resources: ResourceBundle get() = getBundle("string", Locale(locale))

    override fun toString(): String = capitalize(name.replace("_", " ").toLowerCase())

    companion object {
        fun from(locale: String): Language = values().first { it.locale == locale }
    }
}