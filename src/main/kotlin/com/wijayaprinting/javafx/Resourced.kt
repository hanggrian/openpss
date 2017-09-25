package com.wijayaprinting.javafx

import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
interface Resourced {

    val resources: ResourceBundle

    fun getString(key: String): String = resources.getString(key)

    fun getBoolean(key: String): Boolean = getString(key).toBoolean()

    fun getInt(key: String): Int = getString(key).toInt()

    fun getLong(key: String): Long = getString(key).toLong()

    fun getFloat(key: String): Float = getString(key).toFloat()

    fun getDouble(key: String): Double = getString(key).toDouble()
}