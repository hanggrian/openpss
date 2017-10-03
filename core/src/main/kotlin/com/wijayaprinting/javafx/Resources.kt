@file:JvmName("ResourcesKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx

import java.util.*

@PublishedApi internal var mResources: ResourceBundle? = null

inline fun initResources(resources: ResourceBundle) {
    if (mResources != null) throw UnsupportedOperationException("initResources() once only!")
    mResources = resources
}

inline val resources: ResourceBundle get() = mResources!!

inline fun getString(key: String): String = resources.getString(key)
inline fun getBoolean(key: String): Boolean = getString(key).toBoolean()
inline fun getInt(key: String): Int = getString(key).toInt()
inline fun getLong(key: String): Long = getString(key).toLong()
inline fun getFloat(key: String): Float = getString(key).toFloat()
inline fun getDouble(key: String): Double = getString(key).toDouble()