@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import java.lang.Character.isDigit

inline fun String.tidy(): String = replace(Regex("\\s+"), " ").trim()

inline fun String.capitalizeAll(): String = split(" ").joinToString(" ") { it.capitalize() }

inline fun String.withoutCurrency(): String = substring(indexOf(toCharArray().first { isDigit(it) }))