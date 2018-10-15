package com.hendraanggrian.openpss.control

import ktfx.NodeManager

interface ActionManager {

    /** Override this function to add extra actions. */
    fun NodeManager.onCreateActions() {
    }
}