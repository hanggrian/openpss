package com.hanggrian.openpss.control

import com.jfoenix.effects.JFXDepthManager
import ktfx.jfoenix.layouts.KtfxJfxToolbar

class Toolbar : KtfxJfxToolbar() {
    init {
        JFXDepthManager.setDepth(this, 0)
    }
}
