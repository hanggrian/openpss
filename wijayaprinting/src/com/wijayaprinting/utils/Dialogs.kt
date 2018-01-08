@file:Suppress("NOTHING_TO_INLINE")

package com.wijayaprinting.utils

import javafx.scene.control.ButtonType.CLOSE
import javafx.scene.control.ButtonType.OK
import javafx.scene.control.Dialog
import kotfx.button
import kotfx.buttons

/** Apparently dialog won't close without a button. */
inline fun Dialog<*>.forceClose() {
    if (buttons.none { it.buttonData.isCancelButton }) button(CLOSE)
    if (buttons.none { it.buttonData.isDefaultButton }) button(OK)
    close()
}