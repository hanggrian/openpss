package com.hendraanggrian.openpss.popup

import ktfx.NodeManager

interface PopupLifecycle {

    fun onCreate(manager: NodeManager) {
    }

    fun onCreateActions(manager: NodeManager) {
    }
}