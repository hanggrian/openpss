package com.hendraanggrian.openpss.content

import ktfx.NodeInvokable

interface ActionManager {

    /** Override this function to add actions. */
    fun NodeInvokable.onCreateActions() {
    }
}