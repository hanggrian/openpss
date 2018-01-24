package com.wijayaprinting.ui

import javafx.scene.text.Font
import javafx.scene.text.Font.loadFont
import java.io.InputStream
import java.net.URL
import java.util.*

interface Resourced {

    val resources: ResourceBundle

    fun getString(key: String): String = resources.getString(key)
    fun getBoolean(key: String): Boolean = getString(key).toBoolean()
    fun getInt(key: String): Int = getString(key).toInt()
    fun getLong(key: String): Long = getString(key).toLong()
    fun getFloat(key: String): Float = getString(key).toFloat()
    fun getDouble(key: String): Double = getString(key).toDouble()

    fun getStrings(vararg keys: String): Array<String> = keys.map { getString(it) }.toTypedArray()

    fun getResource(name: String): URL = javaClass.getResource(name)
    fun getResourceAsStream(name: String): InputStream = javaClass.getResourceAsStream(name)

    fun getExternalForm(id: String): String = getResource(id).toExternalForm()

    fun getFont(font: String, size: Number): Font = loadFont(getExternalForm(font), size.toDouble())
}