@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.StringProperty
import javafxx.beans.binding.booleanBindingOf

/** Reversed version of [String.orEmpty]. */
inline fun String.orNull(): String? = if (isBlank()) null else this

/** Remove trailing and double whitespaces. */
inline fun String.clean(): String = replace("\\s+".toRegex(), " ").trim()

/** User's name must be at least 2 words. */
inline fun String.isName(): Boolean = split(" ").let { it.size > 1 && it.all { it.isNotEmpty() } }

inline fun StringProperty.isName(): BooleanBinding = booleanBindingOf(this) { value.isName() }