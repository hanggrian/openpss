package com.wijayaprinting.javafx

import javafx.collections.ObservableList
import kotfx.collections.observableListOf
import java.util.*

data class Language(val locale: String, val name: String) {

    override fun toString(): String = name

    fun getResources(name: String): ResourceBundle = ResourceBundle.getBundle(name, Locale(locale))

    fun getResources(name: String, classLoader: ClassLoader): ResourceBundle = ResourceBundle.getBundle(name, Locale(locale), classLoader)

    companion object {
        const val LOCALE_EN = "en"
        const val LOCALE_IN = "in"

        fun listAll(): ObservableList<Language> = observableListOf(
                Language(LOCALE_EN, "English"),
                Language(LOCALE_IN, "Indonesia"))

        fun parse(locale: String): Language = listAll().first { it.locale == locale }
    }
}