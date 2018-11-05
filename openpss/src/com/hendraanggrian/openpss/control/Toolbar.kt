package com.hendraanggrian.openpss.control

import com.jfoenix.controls.JFXToolbar
import com.jfoenix.effects.JFXDepthManager

class Toolbar : JFXToolbar() {

    init {
        JFXDepthManager.setDepth(this, 0)
    }
}