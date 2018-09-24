package com.hendraanggrian.openpss.control

import javafx.scene.Node
import javafxx.layouts.LayoutManager

interface ActionManager {

    /** Override this function to add extra actions. */
    fun LayoutManager<Node>.onCreateActions() {
    }
}