@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.manager

import java.util.*

@PublishedApi internal var mResources: ResourceBundle? = null

inline fun setResources(resources: ResourceBundle) {
    checkNotNull(resources)
    mResources = resources
}

inline val resources: ResourceBundle get() = mResources!!

inline fun getString(key: String): String = mResources?.getString(key) ?: "null"
inline fun getBoolean(key: String): Boolean = mResources?.getString(key)?.toBoolean() ?: false
inline fun getInt(key: String): Int = mResources?.getString(key)?.toInt() ?: Int.MIN_VALUE
inline fun getLong(key: String): Long = mResources?.getString(key)?.toLong() ?: Long.MIN_VALUE
inline fun getFloat(key: String): Float = mResources?.getString(key)?.toFloat() ?: Float.MIN_VALUE
inline fun getDouble(key: String): Double = mResources?.getString(key)?.toDouble() ?: Double.MIN_VALUE