package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.Language
import java.util.ResourceBundle

/** Easier access to [ResourceBundle] across app. */
interface Resourced {

    val language: Language
    val resources: ResourceBundle

    fun getString(key: String): String = resources.getString(key)
    fun getBoolean(key: String): Boolean = getString(key).toBoolean()
    fun getInt(key: String): Int = getString(key).toInt()
    fun getLong(key: String): Long = getString(key).toLong()
    fun getFloat(key: String): Float = getString(key).toFloat()
    fun getDouble(key: String): Double = getString(key).toDouble()

    fun getStringArray(vararg keys: String): Array<String> = keys.map { getString(it) }.toTypedArray()
    fun getBooleanArray(vararg keys: String): Array<Boolean> = keys.map { getBoolean(it) }.toTypedArray()
    fun getIntArray(vararg keys: String): Array<Int> = keys.map { getInt(it) }.toTypedArray()
    fun getLongArray(vararg keys: String): Array<Long> = keys.map { getLong(it) }.toTypedArray()
    fun getFloatArray(vararg keys: String): Array<Float> = keys.map { getFloat(it) }.toTypedArray()
    fun getDoubleArray(vararg keys: String): Array<Double> = keys.map { getDouble(it) }.toTypedArray()
}