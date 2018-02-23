package com.wijayaprinting.util

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCode.BACK_SPACE
import javafx.scene.input.KeyCode.DELETE

inline val KeyCode.isDelete: Boolean get() = this == DELETE || this == BACK_SPACE