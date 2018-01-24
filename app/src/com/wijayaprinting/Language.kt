package com.wijayaprinting

import java.util.*
import java.util.ResourceBundle.getBundle

enum class Language(val locale: String, private val country: String) {
    ENGLISH("en", "English"),
    INDONESIA("in", "Indonesia");

    val resources: ResourceBundle get() = getBundle("string", Locale(locale))

    override fun toString(): String = country

    companion object {
        fun from(locale: String): Language = values().first { it.locale == locale }
    }
}