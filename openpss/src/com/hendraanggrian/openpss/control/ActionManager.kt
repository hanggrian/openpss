package com.hendraanggrian.openpss.control

import ktfx.NodeManager

interface ActionManager {

    /** Override this function to add actions. */
    fun NodeManager.onCreateActions() {
    }
}