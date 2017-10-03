@file:JvmName("TextFieldsKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx.utils

import javafx.scene.control.TextField
import org.apache.commons.lang3.math.NumberUtils

/**
 * Disallow non-digits input, should only be used if text of this field is intended to be non-decimal number.
 * @see com.wijayaprinting.javafx.control.field.IntField
 * @see com.wijayaprinting.javafx.control.field.LongField
 */
inline fun TextField.digitsOnly() = textProperty().addListener { _, _, newValue ->
    if (!isDigits) text = newValue.replace("[^\\d]".toRegex(), "")
}

inline val TextField.isDigits: Boolean get() = NumberUtils.isDigits(text)

inline val TextField.isDecimal: Boolean get() = NumberUtils.isCreatable(text)