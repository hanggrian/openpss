@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.StringProperty
import ktfx.beans.binding.booleanBindingOf

/** Remove trailing and double whitespaces. */
inline fun String.clean(): String = replace("\\s+".toRegex(), " ").trim()

/** User's name must be at least 2 words. */
inline fun String.isName(): Boolean = split(" ").let { it.size > 1 && it.all { it.isNotEmpty() } }

inline fun StringProperty.isName(): BooleanBinding = booleanBindingOf(this) { value.isName() }