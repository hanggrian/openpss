@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

/** Remove trailing and double whitespaces. */
inline fun String.clean(): String = replace("\\s+".toRegex(), " ").trim()

/** User's name must be at least 2 words. */
inline fun String.isName(): Boolean = split(" ").let { it.size > 1 && it.all { it.isNotEmpty() } }