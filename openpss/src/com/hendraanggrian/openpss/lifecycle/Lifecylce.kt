package com.hendraanggrian.openpss.lifecycle

import ktfx.NodeManager

interface Lifecylce {

    fun onCreate(manager: NodeManager) {
    }

    fun onCreateActions(manager: NodeManager) {
    }
}