@file:JvmName("Extras")
@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package com.wijayaprinting.javafx

@PublishedApi internal var mExtra: Any? = null

inline fun setExtra(extra: Any) {
    mExtra = extra
}

inline fun <T> getExtra(): T {
    val extra = mExtra as T
    mExtra = null
    return extra
}