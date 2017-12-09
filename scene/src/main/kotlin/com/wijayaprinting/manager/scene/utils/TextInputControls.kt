@file:JvmName("TextInputControlsKt")
@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.manager.scene.utils

import javafx.scene.control.TextInputControl
import org.apache.commons.lang3.math.NumberUtils

/**
 * Disallow non-digits input, should only be used if text of this field is intended to be non-decimal number.
 *
 * @see com.wijayaprinting.manager.scene.control.IntField
 * @see com.wijayaprinting.manager.scene.control.LongField
 */
inline fun TextInputControl.digitsOnly() = textProperty().addListener { _, _, newValue ->
    text = when {
        !isDigits -> if (newValue.isEmpty()) "0" else newValue.replace("[^\\d]".toRegex(), "")
        else -> newValue.toLong().toString()
    }
}

inline val TextInputControl.isDigits: Boolean get() = NumberUtils.isDigits(text)

inline val TextInputControl.isDecimal: Boolean
    get() {
        /** Not supported with SceneBuilder!. */
        // NumberUtils.isCreatable(text)
        return try {
            java.lang.Double.parseDouble(text)
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

inline val TextInputControl.textOrNull: String? get() = if (text.isEmpty()) null else text