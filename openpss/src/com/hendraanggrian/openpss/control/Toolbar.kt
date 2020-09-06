package com.hendraanggrian.openpss.control

import com.jfoenix.effects.JFXDepthManager
import ktfx.jfoenix.layouts.KtfxJFXToolbar

class Toolbar : KtfxJFXToolbar() {

    init {
        JFXDepthManager.setDepth(this, 0)
    }
}
