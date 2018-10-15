package com.hendraanggrian.openpss.popup

import ktfx.NodeManager
import ktfx.application.later

interface PopupLifecycle {

    fun onCreate(manager: NodeManager) {
    }

    fun onCreateActions(manager: NodeManager) {
    }

    fun NodeManager.runLater(run: NodeManager.() -> Unit) = later { run() }
}