@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.utils

import javafx.scene.input.MouseButton.PRIMARY
import javafx.scene.input.MouseEvent

inline fun MouseEvent.isDoubleClick(): Boolean = button == PRIMARY && clickCount == 2