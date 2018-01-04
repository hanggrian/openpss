package com.wijayaprinting.manager

import com.wijayaprinting.Registrable
import java.net.URL
import java.util.*

/** Easy access to [ResourceBundle] in application and controllers. */
interface Component : Registrable {

    val resources: ResourceBundle

    fun getString(key: String): String = resources.getString(key)
    fun getBoolean(key: String): Boolean = getString(key).toBoolean()
    fun getInt(key: String): Int = getString(key).toInt()
    fun getLong(key: String): Long = getString(key).toLong()
    fun getFloat(key: String): Float = getString(key).toFloat()
    fun getDouble(key: String): Double = getString(key).toDouble()

    fun getResource(name: String): URL = App::class.java.getResource(name)
}