package com.hendraanggrian.openpss.util

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.StringProperty
import ktfx.toBoolean

/** Reversed name of [String.orEmpty]. */
fun String.orNull(): String? = if (isBlank()) null else this

/** Remove trailing and double whitespaces. */
fun String.clean(): String = replace("\\s+".toRegex(), " ").trim()

/** User's name must be at least 2 words. */
fun String.isPersonName(): Boolean {
    val parts = clean().split(" ")
    return parts.size > 1 && parts.all { part ->
        val firstUppercase = part.first().isUpperCase()
        when {
            part.length > 1 -> firstUppercase &&
                part.removeRange(0, 1).all { it.isLowerCase() }
            else -> firstUppercase
        }
    }
}

val StringProperty.personNameBinding: BooleanBinding get() = toBoolean { it!!.isPersonName() }
