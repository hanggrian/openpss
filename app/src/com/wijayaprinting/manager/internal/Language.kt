package com.wijayaprinting.manager.internal

import javafx.collections.ObservableList
import kotfx.observableListOf
import java.util.*
import java.util.ResourceBundle.getBundle

data class Language(val locale: String, private val name: String) {

    override fun toString(): String = name

    fun getResources(name: String): ResourceBundle = getBundle(name, Locale(locale))

    fun getResources(name: String, classLoader: ClassLoader): ResourceBundle = getBundle(name, Locale(locale), classLoader)

    companion object {
        const val LOCALE_EN = "en"
        const val LOCALE_IN = "in"

        fun listAll(): ObservableList<Language> = observableListOf(
                Language(LOCALE_EN, "English"),
                Language(LOCALE_IN, "Indonesia"))

        fun parse(locale: String): Language = listAll().first { it.locale == locale }
    }
}