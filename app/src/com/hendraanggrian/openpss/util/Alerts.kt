@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.YES
import ktfx.scene.control.confirmAlert

inline fun yesNoAlert(contentText: String, noinline action: () -> Unit) = confirmAlert(contentText, YES, NO)
    .showAndWait()
    .filter { it == YES }
    .ifPresent { action() }