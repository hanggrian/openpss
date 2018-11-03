package com.hendraanggrian.openpss.control

import ktfx.NodeInvokable

interface ActionManager {

    /** Override this function to add actions. */
    fun NodeInvokable.onCreateActions() {
    }
}