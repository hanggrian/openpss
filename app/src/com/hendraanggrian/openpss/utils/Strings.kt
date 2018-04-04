@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.utils

/** Remove trailing and double whitespaces. */
inline fun String.clean(): String = replace(Regex("\\s+"), " ").trim()

/** Capitalize words from a sentence. */
inline fun String.capitalizeAll(): String = split(" ").joinToString(" ") { it.capitalize() }

/** User's name must be at least 2 words. */
inline fun String.isName(): Boolean = split(" ").size > 1