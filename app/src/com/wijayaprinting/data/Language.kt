package com.wijayaprinting.data

import com.wijayaprinting.core.Listable
import javafx.collections.ObservableList
import kotfx.observableListOf
import java.util.*
import java.util.ResourceBundle.getBundle

data class Language(val locale: String, private val name: String) {

    fun getResources(name: String): ResourceBundle = getBundle(name, Locale(locale))

    override fun toString(): String = name

    companion object : Listable<Language> {
        const val LOCALE_EN = "en"
        const val LOCALE_IN = "in"

        override fun listAll(): ObservableList<Language> = observableListOf(
                Language(LOCALE_EN, "English"),
                Language(LOCALE_IN, "Indonesia"))

        fun valueOf(locale: String): Language = listAll().first { it.locale == locale }
    }
}