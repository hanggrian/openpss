@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

inline fun String.tidy(): String = replace(Regex("\\s+"), " ").trim()

inline fun String.capitalizeAll(): String = split(" ").joinToString(" ") { it.capitalize() }