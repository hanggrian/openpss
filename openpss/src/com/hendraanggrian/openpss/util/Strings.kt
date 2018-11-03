@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.StringProperty
import ktfx.beans.binding.buildBooleanBinding

/** Reversed version of [String.orEmpty]. */
fun String.orNull(): String? = if (isBlank()) null else this

/** Remove trailing and double whitespaces. */
fun String.clean(): String = replace("\\s+".toRegex(), " ").trim()

/** User's name must be at least 2 words. */
fun String.isName(): Boolean = split(" ").let { s -> s.size > 1 && s.all { it.isNotEmpty() } }

fun StringProperty.isName(): BooleanBinding = buildBooleanBinding(this) { value.isName() }