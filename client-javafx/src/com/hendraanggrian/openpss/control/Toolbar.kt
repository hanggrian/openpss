@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.control

import com.jfoenix.effects.JFXDepthManager
import ktfx.jfoenix.KtfxJFXToolbar

class Toolbar : KtfxJFXToolbar() {

    init {
        JFXDepthManager.setDepth(this, 0)
    }
}
