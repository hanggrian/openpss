@file:JvmName("TextInputControlsKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.javafx.scene.utils

import javafx.scene.control.TextInputControl
import org.apache.commons.lang3.math.NumberUtils

/**
 * Disallow non-digits input, should only be used if text of this field is intended to be non-decimal number.
 * @see com.wijayaprinting.javafx.scene.control.IntField
 * @see com.wijayaprinting.javafx.scene.control.LongField
 */
inline fun TextInputControl.digitsOnly() = textProperty().addListener { _, _, newValue ->
    if (!isDigits) text = newValue.replace("[^\\d]".toRegex(), "")
}

inline val TextInputControl.isDigits: Boolean get() = NumberUtils.isDigits(text)

inline val TextInputControl.isDecimal: Boolean
    get() {
        /** Not supported with SceneBuilder!. */
        // NumberUtils.isCreatable(text)
        try {
            java.lang.Double.parseDouble(text)
            return true
        } catch (e: NumberFormatException) {
            return false
        }
    }

inline val TextInputControl.textOrNull: String? get() = if (text.isEmpty()) null else text