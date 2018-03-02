@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.util

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCode.BACK_SPACE
import javafx.scene.input.KeyCode.DELETE

inline fun KeyCode.isDelete(): Boolean = this == DELETE || this == BACK_SPACE